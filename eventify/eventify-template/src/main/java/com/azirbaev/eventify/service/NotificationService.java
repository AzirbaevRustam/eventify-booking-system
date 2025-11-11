package com.azirbaev.eventify.service;

import com.azirbaev.eventify.dto.notification.NotificationPreferences;
import com.azirbaev.eventify.entity.NotificationPreference;
import com.azirbaev.eventify.entity.User;
import com.azirbaev.eventify.exception.NotificationPreferences.NotificationPreferencesNotFoundException;
import com.azirbaev.eventify.exception.UserNotFoundException;
import com.azirbaev.eventify.mapper.NotificationMapper;
import com.azirbaev.eventify.repository.NotificationPreferenceRepository;
import com.azirbaev.eventify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationPreferenceRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public NotificationPreferences getPreferences(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        Optional<NotificationPreference> preferencesOpt = notificationRepository.findByUserId(userId);

        if (preferencesOpt.isEmpty()) {
            NotificationPreference defaultPreferences = createDefaultPreferences(userId);
            NotificationPreference savedPreferences = notificationRepository.save(defaultPreferences);
            return notificationMapper.toDto(savedPreferences);
        }

        return notificationMapper.toDto(preferencesOpt.get());
    }

    private NotificationPreference createDefaultPreferences(Long userId) {

        User user = userRepository.getReferenceById(userId);

        NotificationPreference preferences = new NotificationPreference();
        preferences.setUser(user);
        preferences.setNotifyNewEvents(true);
        preferences.setNotifyUpcoming(true);
        preferences.setNotifyBeforeHours(1);

        return preferences;
    }

    @Transactional
    public NotificationPreferences updatePreferences(Long userId, NotificationPreferences request) {
        log.info("Updating notification preferences for user ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        if (request.getNotifyBeforeHours() != null &&
            (request.getNotifyBeforeHours() < 1 || request.getNotifyBeforeHours() > 24)) {
            throw new IllegalArgumentException("notifyBeforeHours must be between 1 and 24");
        }

        Optional<NotificationPreference> preferencesOpt = notificationRepository.findByUserId(userId);

        if (preferencesOpt.isPresent()) {
            NotificationPreference entity = preferencesOpt.get();
            updateEntityFromRequest(entity, request);

            NotificationPreference saved = notificationRepository.save(entity);
            log.info("Notification preferences updated for user ID: {}", userId);

            return notificationMapper.toDto(saved);
        } else {
            NotificationPreference newEntity = new NotificationPreference();
            newEntity.setUser(userRepository.getReferenceById(userId));
            updateEntityFromRequest(newEntity, request);

            NotificationPreference saved = notificationRepository.save(newEntity);
            log.info("New notification preferences created for user ID: {}", userId);

            return notificationMapper.toDto(saved);
        }
    }

    private void updateEntityFromRequest(NotificationPreference entity, NotificationPreferences request) {
        if (request.getNotifyNewEvents() != null) {
            entity.setNotifyNewEvents(request.getNotifyNewEvents());
        }
        if (request.getNotifyUpcoming() != null) {
            entity.setNotifyUpcoming(request.getNotifyUpcoming());
        }
        if (request.getNotifyBeforeHours() != null) {
            entity.setNotifyBeforeHours(request.getNotifyBeforeHours());
        }
    }

    @Transactional
    public void deletePreferences(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        Optional<NotificationPreference> preferences = notificationRepository.findByUserId(userId);

        if (preferences.isPresent()) {
            notificationRepository.delete(preferences.get());
            log.info("Notification preferences deleted for user ID: {}", userId);
        } else {
            throw new NotificationPreferencesNotFoundException("Настройки не найдены");
        }
    }
}
