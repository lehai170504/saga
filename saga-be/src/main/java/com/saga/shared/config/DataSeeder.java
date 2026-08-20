package com.saga.shared.config;

import com.saga.user.entity.Role;
import com.saga.user.entity.UserStatus;
import com.saga.user.entity.User;
import com.saga.user.repository.JpaUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(JpaUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        seedUser("admin@saga.local", "Saga Admin", Role.ADMIN, "123456aA@");
        seedUser("lecturer@saga.local", "Saga Lecturer", Role.LECTURER, "123456aA@");
    }

    private void seedUser(String email, String name, Role role, String rawPassword) {
        if (!userRepository.findByEmail(email).isPresent()) {
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setEmail(email);
            user.setName(name);
            user.setRole(role);
            user.setStatus(UserStatus.ACTIVE);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setPicture("https://ui-avatars.com/api/?name=" + name.replace(" ", "+"));
            userRepository.save(user);
        }
    }
}
