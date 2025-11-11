package com.azirbaev.eventify.dto.event;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateRequest {
    private String title;
    private String description;
    private LocalDateTime dateTime;

    @Min(1)
    private Integer totalTickets;

    private String coverUrl;
}
