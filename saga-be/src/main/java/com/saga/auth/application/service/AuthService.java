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

    private static final Pattern STUDENT_EMAIL_PATTERN = Pattern.compile("^[a-zA-Z]+([a-zA-Z]{2}\\d{6})@fpt\\.edu\\.vn$", Pattern.CASE_INSENSITIVE);

    public AuthService(GoogleAuthPort googleAuthPort, 
                       UserRepositoryPort userRepositoryPort,
                       StudentRepositoryPort studentRepositoryPort,
                       LecturerRepositoryPort lecturerRepositoryPort,
                       JwtProviderPort jwtProviderPort) {
        this.googleAuthPort = googleAuthPort;
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

            if (email.endsWith("@fpt.edu.vn")) {
                user.setRole(Role.STUDENT);
                user = userRepositoryPort.save(user);
                
                String studentCode = extractStudentCode(email);
                Student student = Student.builder()
                        .id(UUID.randomUUID())
                        .userId(user.getId())
                        .studentCode(studentCode)
                        .build();
                studentRepositoryPort.save(student);
                
            } else if (email.endsWith("@fe.edu.vn")) {
                user.setRole(Role.LECTURER);
                user = userRepositoryPort.save(user);
                
                Lecturer lecturer = Lecturer.builder()
                        .id(UUID.randomUUID())
                        .userId(user.getId())
                        .build();
                lecturerRepositoryPort.save(lecturer);
            } else {
                throw new UnauthorizedException("Invalid email domain.");
            }
        }

        if (UserStatus.BANNED.equals(user.getStatus())) {
            throw new UnauthorizedException("Your account is banned.");
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
}
