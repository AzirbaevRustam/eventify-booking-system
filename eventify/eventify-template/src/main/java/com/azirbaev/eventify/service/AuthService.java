package com.azirbaev.eventify.service;

import com.azirbaev.eventify.dto.auth.AuthResponse;
import com.azirbaev.eventify.dto.auth.LoginRequest;
import com.azirbaev.eventify.dto.auth.RegisterRequest;
import com.azirbaev.eventify.entity.User;
import com.azirbaev.eventify.entity.UserStatus;
import com.azirbaev.eventify.exception.auth.InvalidCredentialsException;
import com.azirbaev.eventify.exception.auth.UserAlreadyExistsException;
import com.azirbaev.eventify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("Пользователь уже существует!");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(encodedPassword);
        user.setRole(UserStatus.USER);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        AuthResponse response = new AuthResponse();
        String token = "user_" + user.getId() + "_" + System.currentTimeMillis();
        response.setToken(token);
        response.setRole(savedUser.getRole().name());
        return response;
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isEmpty()) {
            throw new InvalidCredentialsException("Неверные учетные данные");
        }

        User user = existingUser.get();
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException("Неверные учетные данные");
        }

        AuthResponse response = new AuthResponse();
        String token = "user_" + user.getId() + "_" + System.currentTimeMillis();
        response.setToken(token);
        response.setRole(user.getRole().name());
        return response;
    }
}