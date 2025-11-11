package com.azirbaev.eventify.dto.notification;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferences {

    private Boolean notifyNewEvents;
    private Boolean notifyUpcoming;

    @Min(1)
    @Max(24)
    private Integer notifyBeforeHours;
}
