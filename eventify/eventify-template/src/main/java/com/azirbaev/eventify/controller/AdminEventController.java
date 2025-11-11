package com.azirbaev.eventify.controller;

import com.azirbaev.eventify.dto.error.ErrorResponse;
import com.azirbaev.eventify.dto.event.EventCreateRequest;
import com.azirbaev.eventify.dto.event.EventResponse;
import com.azirbaev.eventify.dto.event.EventUpdateRequest;
import com.azirbaev.eventify.exception.UserNotFoundException;
import com.azirbaev.eventify.exception.event.EventNotFoundException;
import com.azirbaev.eventify.exception.event.InvalidEventDateException;
import com.azirbaev.eventify.exception.event.InvalidTicketCountException;
import com.azirbaev.eventify.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestBody @Valid EventCreateRequest request,
            @RequestParam Long adminUserId) {
        try {
            EventResponse event = eventService.createEvent(request, adminUserId);
            return ResponseEntity.status(201).body(event);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("USER_NOT_FOUND", "Пользователь не найден"));
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("EVENT_NOT_FOUND", "Мероприятие не найдено"));
        } catch (AccessDeniedException | InvalidEventDateException | InvalidTicketCountException e) {
            return ResponseEntity.status(400).body(new ErrorResponse("BAD_REQUEST", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @RequestBody @Valid EventUpdateRequest request,
            @RequestParam Long adminUserId) {
        try {
            EventResponse eventResponse = eventService.updateEvent(request, adminUserId, id);
            return ResponseEntity.ok(eventResponse);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("USER_NOT_FOUND", "Пользователь не найден"));
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("EVENT_NOT_FOUND", "Мероприятие не найдено"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(new ErrorResponse("ACCESS_DENIED", "Доступ запрещен"));
        } catch (InvalidEventDateException | InvalidTicketCountException e) {
            return ResponseEntity.status(400).body(new ErrorResponse("BAD_REQUEST", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable Long id,
            @RequestParam Long adminUserId) {
        try {
            eventService.deleteEvent(adminUserId, id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("USER_NOT_FOUND", "Пользователь не найден"));
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("EVENT_NOT_FOUND", "Мероприятие не найдено"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(new ErrorResponse("ACCESS_DENIED", "Доступ запрещен"));
        }
    }
}
