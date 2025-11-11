package com.azirbaev.eventify.dto.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    @Min(1)
    private Integer totalTickets;

    private String coverUrl;
}
