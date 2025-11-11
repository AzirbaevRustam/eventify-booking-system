package com.azirbaev.eventify.controller;

import com.azirbaev.eventify.dto.booking.BookingResponse;
import com.azirbaev.eventify.dto.booking.CreateBookingRequest;
import com.azirbaev.eventify.dto.booking.UpdateBookingRequest;
import com.azirbaev.eventify.dto.error.ErrorResponse;
import com.azirbaev.eventify.exception.UserNotFoundException;
import com.azirbaev.eventify.exception.booking.BookingAlreadyConfirmedException;
import com.azirbaev.eventify.exception.booking.BookingExpiredException;
import com.azirbaev.eventify.exception.booking.BookingNotFoundException;
import com.azirbaev.eventify.exception.booking.NotEnoughTicketsException;
import com.azirbaev.eventify.exception.event.EventNotFoundException;
import com.azirbaev.eventify.exception.event.InvalidEventDateException;
import com.azirbaev.eventify.exception.event.InvalidTicketCountException;
import com.azirbaev.eventify.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<?> getUserBookings(@RequestParam Long userId) {
        try {
            List<BookingResponse> bookings = bookingService.getUserBookings(userId);
            return ResponseEntity.ok(bookings);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("USER_NOT_FOUND", "Пользователь не найден"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody @Valid CreateBookingRequest request, @RequestParam Long userId) {
        try {
            BookingResponse booking = bookingService.createBooking(request, userId);
            return ResponseEntity.status(201).body(booking);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("USER_NOT_FOUND", "Пользователь не найден"));
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("EVENT_NOT_FOUND", "Мероприятие не найдено"));
        } catch (NotEnoughTicketsException | InvalidEventDateException | InvalidTicketCountException e) {
            return ResponseEntity.status(400).body(new ErrorResponse("BAD_REQUEST", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id, @RequestParam Long userId) {
        try {
            BookingResponse bookingResponse = bookingService.getBookingById(id, userId);
            return ResponseEntity.ok(bookingResponse);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("USER_NOT_FOUND", "Пользователь не найден"));
        } catch (BookingNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("BOOKING_NOT_FOUND", "Бронирование не найдено"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(new ErrorResponse("ACCESS_DENIED", "Доступ запрещен"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(
            @PathVariable Long id,
            @RequestBody @Valid UpdateBookingRequest request,
            @RequestParam Long userId) {
        try {
            BookingResponse booking = bookingService.updateBooking(id, request, userId);
            return ResponseEntity.ok(booking);
        } catch (BookingNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("BOOKING_NOT_FOUND", "Бронирование не найдено"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(new ErrorResponse("ACCESS_DENIED", "Доступ запрещен"));
        } catch (BookingAlreadyConfirmedException | BookingExpiredException | InvalidTicketCountException |
                 NotEnoughTicketsException e) {
            return ResponseEntity.status(400).body(new ErrorResponse("BAD_REQUEST", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, @RequestParam Long userId) {
        try {
            bookingService.cancelBooking(id, userId);
            return ResponseEntity.noContent().build();
        } catch (BookingNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("BOOKING_NOT_FOUND", "Бронирование не найдено"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(new ErrorResponse("ACCESS_DENIED", "Доступ запрещен"));
        }
    }
}