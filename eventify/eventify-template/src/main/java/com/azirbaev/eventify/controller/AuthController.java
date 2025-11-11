package com.azirbaev.eventify.controller;

import com.azirbaev.eventify.dto.auth.AuthResponse;
import com.azirbaev.eventify.dto.auth.LoginRequest;
import com.azirbaev.eventify.dto.auth.RegisterRequest;
import com.azirbaev.eventify.dto.error.ErrorResponse;
import com.azirbaev.eventify.exception.auth.InvalidCredentialsException;
import com.azirbaev.eventify.exception.auth.UserAlreadyExistsException;
import com.azirbaev.eventify.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        try {
            AuthResponse register = authService.register(request);
            return ResponseEntity.status(201).body(register);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(400).body(new ErrorResponse("USER_EXISTS", "Пользователь уже существует"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        try {
            AuthResponse login = authService.login(request);
            return ResponseEntity.ok(login);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(401).body(new ErrorResponse("INVALID_CREDENTIALS", "Неверный email или пароль"));
        }
    }
}
