package com.saga.auth.service;

import com.saga.user.repository.JpaUserRepository;
import com.saga.user.repository.JpaStudentRepository;
import com.saga.user.repository.JpaLecturerRepository;

import com.saga.auth.dto.AuthResponse;
import com.saga.auth.dto.GoogleLoginRequest;
import com.saga.auth.dto.UserProfileDTO;
import com.saga.user.entity.Lecturer;
import com.saga.user.entity.Role;
import com.saga.user.entity.Student;
import com.saga.user.entity.User;
import com.saga.user.entity.UserStatus;
import com.saga.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.saga.auth.dto.LocalLoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService implements LoginUseCase {

    private final GoogleAuthService googleAuthPort;
    private final JpaUserRepository userRepository;
    private final JpaStudentRepository studentRepository;
    private final JpaLecturerRepository lecturerRepository;
    private final JwtProviderService jwtProviderPort;
    private final PasswordEncoder passwordEncoder;

    @Value("#{'${app.auth.admin-emails:}'.split(',')}")
    private List<String> adminEmails;

    @Value("#{'${app.auth.lecturer-emails:}'.split(',')}")
    private List<String> lecturerEmails;

    private static final Pattern STUDENT_EMAIL_PATTERN = Pattern
            .compile("^[a-zA-Z]+([a-zA-Z]{2}\\d{6})@fpt\\.edu\\.vn$", Pattern.CASE_INSENSITIVE);

    public AuthService(GoogleAuthService googleAuthPort,
            JpaUserRepository userRepository,
            JpaStudentRepository studentRepository,
            JpaLecturerRepository lecturerRepository,
            JwtProviderService jwtProviderPort,
            PasswordEncoder passwordEncoder) {
        this.googleAuthPort = googleAuthPort;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.lecturerRepository = lecturerRepository;
        this.jwtProviderPort = jwtProviderPort;
    }

    @Override
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        UserProfileDTO profile = googleAuthPort.verifyToken(request.getIdToken());
        String email = profile.getEmail().toLowerCase();

        User user = userRepository.findByEmail(email).orElse(null);

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
                user = userRepository.save(user);
            } else if (lecturerEmails.contains(email) || email.endsWith("@fe.edu.vn")) {
                user.setRole(Role.LECTURER);
                user = userRepository.save(user);

                Lecturer lecturer = Lecturer.builder()
                        .id(UUID.randomUUID())
                        .userId(user.getId())
                        .build();
                lecturerRepository.save(lecturer);
            } else if (email.endsWith("@fpt.edu.vn") || email.endsWith("@gmail.com")) { // Allowing @gmail.com for
                user.setRole(Role.STUDENT);
                user = userRepository.save(user);

                String studentCode = extractStudentCode(email);
                Student student = Student.builder()
                        .id(UUID.randomUUID())
                        .userId(user.getId())
                        .studentCode(studentCode)
                        .build();
                studentRepository.save(student);
            } else {
                throw new UnauthorizedException("Invalid email domain or not whitelisted.");
            }
        }

        if (UserStatus.BANNED.equals(user.getStatus())) {
            throw new UnauthorizedException("Your account is banned.");
        }

        if (UserStatus.PENDING.equals(user.getStatus())) {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
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
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new UnauthorizedException("Your account is banned. Please contact the administrator.");
        }

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
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