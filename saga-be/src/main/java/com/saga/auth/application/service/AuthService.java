package com.saga.auth.application.service;

import com.saga.auth.application.port.GoogleAuthPort;
import com.saga.auth.application.port.JwtProviderPort;
import com.saga.user.application.port.UserRepositoryPort;
import com.saga.user.application.port.StudentRepositoryPort;
import com.saga.user.application.port.LecturerRepositoryPort;

import com.saga.auth.application.dto.AuthResponse;
import com.saga.auth.application.dto.GoogleLoginRequest;
import com.saga.auth.application.dto.UserProfileDTO;
import com.saga.auth.application.usecase.LoginUseCase;
import com.saga.user.domain.Lecturer;
import com.saga.user.domain.Role;
import com.saga.user.domain.Student;
import com.saga.user.domain.User;
import com.saga.user.domain.UserStatus;
import com.saga.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.saga.auth.application.dto.LocalLoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService implements LoginUseCase {

    private final GoogleAuthPort googleAuthPort;
    private final UserRepositoryPort userRepositoryPort;
    private final StudentRepositoryPort studentRepositoryPort;
    private final LecturerRepositoryPort lecturerRepositoryPort;
    private final JwtProviderPort jwtProviderPort;
    private final PasswordEncoder passwordEncoder;

    @Value("#{'${app.auth.admin-emails:}'.split(',')}")
    private List<String> adminEmails;

    @Value("#{'${app.auth.lecturer-emails:}'.split(',')}")
    private List<String> lecturerEmails;

    private static final Pattern STUDENT_EMAIL_PATTERN = Pattern
            .compile("^[a-zA-Z]+([a-zA-Z]{2}\\d{6})@fpt\\.edu\\.vn$", Pattern.CASE_INSENSITIVE);

    public AuthService(GoogleAuthPort googleAuthPort,
            UserRepositoryPort userRepositoryPort,
            StudentRepositoryPort studentRepositoryPort,
            LecturerRepositoryPort lecturerRepositoryPort,
            JwtProviderPort jwtProviderPort,
            PasswordEncoder passwordEncoder) {
        this.googleAuthPort = googleAuthPort;
        this.passwordEncoder = passwordEncoder;
        this.userRepositoryPort = userRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
        this.lecturerRepositoryPort = lecturerRepositoryPort;
        this.jwtProviderPort = jwtProviderPort;
    }

    @Override
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        UserProfileDTO profile = googleAuthPort.verifyToken(request.getIdToken());
        String email = profile.getEmail().toLowerCase();

        User user = userRepositoryPort.findByEmail(email).orElse(null);

        if (user == null) {
            user = User.builder()
                    .id(UUID.randomUUID())
                    .email(email)
                    .name(profile.getName())
                    .picture(profile.getPicture())
                    .status(UserStatus.ACTIVE)
                    .build();

            if (adminEmails.contains(email)) {
                user.setRole(Role.ADMIN);
                user = userRepositoryPort.save(user);
            } else if (lecturerEmails.contains(email) || email.endsWith("@fe.edu.vn")) {
                user.setRole(Role.LECTURER);
                user = userRepositoryPort.save(user);

                Lecturer lecturer = Lecturer.builder()
                        .id(UUID.randomUUID())
                        .userId(user.getId())
                        .build();
                lecturerRepositoryPort.save(lecturer);
            } else if (email.endsWith("@fpt.edu.vn") || email.endsWith("@gmail.com")) { // Allowing @gmail.com for
                                                                                        // student test accounts as well
                user.setRole(Role.STUDENT);
                user = userRepositoryPort.save(user);

                String studentCode = extractStudentCode(email);
                Student student = Student.builder()
                        .id(UUID.randomUUID())
                        .userId(user.getId())
                        .studentCode(studentCode)
                        .build();
                studentRepositoryPort.save(student);
            } else {
                throw new UnauthorizedException("Invalid email domain or not whitelisted.");
            }
        }

        if (UserStatus.BANNED.equals(user.getStatus())) {
            throw new UnauthorizedException("Your account is banned.");
        }

        if (UserStatus.PENDING.equals(user.getStatus())) {
            user.setStatus(UserStatus.ACTIVE);
            userRepositoryPort.save(user);
        }

        String token = jwtProviderPort.generateToken(user);

        return AuthResponse.builder()
                .accessToken(token)
                .role(user.getRole().name())
                .user(UserProfileDTO.builder()
                        .email(user.getEmail())
                        .name(user.getName())
                        .picture(user.getPicture())
                        .build())
                .build();
    }

    private String extractStudentCode(String email) {
        Matcher matcher = STUDENT_EMAIL_PATTERN.matcher(email);
        if (matcher.matches()) {
            return matcher.group(1).toUpperCase();
        }
        return email.substring(0, email.indexOf("@")).toUpperCase();
    }

    @Override
    @Transactional
    public AuthResponse loginLocal(LocalLoginRequest request) {
        User user = userRepositoryPort.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new UnauthorizedException("Your account is banned. Please contact the administrator.");
        }

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            user.setStatus(UserStatus.ACTIVE);
            userRepositoryPort.save(user);
        }

        String token = jwtProviderPort.generateToken(user);
        return AuthResponse.builder()
                .accessToken(token)
                .role(user.getRole().name())
                .user(UserProfileDTO.builder()
                        .email(user.getEmail())
                        .name(user.getName())
                        .picture(user.getPicture())
                        .build())
                .build();
    }

}