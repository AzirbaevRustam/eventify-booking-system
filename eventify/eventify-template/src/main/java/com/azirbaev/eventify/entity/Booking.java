package com.azirbaev.eventify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "ticket_count")
    private Integer ticketCount;

    @Column(name = "confirmed")
    private Boolean confirmed;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
