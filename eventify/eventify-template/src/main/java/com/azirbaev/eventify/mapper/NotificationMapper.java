package com.azirbaev.eventify.mapper;

import com.azirbaev.eventify.dto.notification.NotificationPreferences;
import com.azirbaev.eventify.entity.NotificationPreference;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationPreferences toDto(NotificationPreference entity);

    NotificationPreference toEntity(NotificationPreferences dto);

    void updateEntityFromDto(NotificationPreferences dto, @MappingTarget NotificationPreference entity);
}