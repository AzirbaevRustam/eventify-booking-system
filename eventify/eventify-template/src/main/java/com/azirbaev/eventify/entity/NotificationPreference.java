package com.azirbaev.eventify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "notify_new_events")
    private Boolean notifyNewEvents;

    @Column(name = "notify_upcoming")
    private Boolean notifyUpcoming;

    @Column(name = "notify_before_hours")
    private Integer notifyBeforeHours;
}