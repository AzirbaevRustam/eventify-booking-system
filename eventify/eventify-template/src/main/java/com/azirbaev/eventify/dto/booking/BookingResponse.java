package com.azirbaev.eventify.dto.booking;

import com.azirbaev.eventify.dto.event.EventResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private EventResponse event;
    private String customerEmail;
    private Integer ticketCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiryTime;
    private Boolean confirmed;
    private String timezone;
}
