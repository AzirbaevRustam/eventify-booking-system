package com.azirbaev.eventify.controller;

import com.azirbaev.eventify.dto.error.ErrorResponse;
import com.azirbaev.eventify.dto.notification.NotificationPreferences;
import com.azirbaev.eventify.exception.NotificationPreferences.NotificationPreferencesNotFoundException;
import com.azirbaev.eventify.exception.UserNotFoundException;
import com.azirbaev.eventify.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotificationPreferences(@RequestParam Long userId) {
        try {
            NotificationPreferences preferences = notificationService.getPreferences(userId);
            return ResponseEntity.ok(preferences);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("USER_NOT_FOUND", "Пользователь не найден"));
        }
    }

    @PutMapping("/notifications")
    public ResponseEntity<?> updateNotificationPreferences(
            @RequestBody @Valid NotificationPreferences request,
            @RequestParam Long userId) {
        try {
            NotificationPreferences preferences = notificationService.updatePreferences(userId, request);
            return ResponseEntity.ok(preferences);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("USER_NOT_FOUND", "Пользователь не найден"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new ErrorResponse("BAD_REQUEST", e.getMessage()));
        }
    }

    @DeleteMapping("/notifications")
    public ResponseEntity<?> deleteNotificationPreferences(@RequestParam Long userId) {
        try {
            notificationService.deletePreferences(userId);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("USER_NOT_FOUND", "Пользователь не найден"));
        } catch (NotificationPreferencesNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("NOTIFICATION_PREFERENCES_NOT_FOUND", "Настройки уведомлений не найдены"));
        }

    }
}