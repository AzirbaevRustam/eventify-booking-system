package com.azirbaev.eventify.controller;

import com.azirbaev.eventify.dto.booking.BookingResponse;
import com.azirbaev.eventify.dto.error.ErrorResponse;
import com.azirbaev.eventify.exception.booking.BookingAlreadyConfirmedException;
import com.azirbaev.eventify.exception.booking.BookingExpiredException;
import com.azirbaev.eventify.exception.booking.BookingNotFoundException;
import com.azirbaev.eventify.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {
    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<?> getAllBookings(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Boolean unconfirmedOnly) {
        try {
            List<BookingResponse> allBookings = bookingService.getAllBookings(eventId, unconfirmedOnly);
            return ResponseEntity.ok(allBookings);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("SERVER_ERROR", "Внутренняя ошибка сервера"));
        }
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBooking(
            @PathVariable Long id) {
        try {
            bookingService.confirmBooking(id);
            return ResponseEntity.noContent().build();
        } catch (BookingNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("BOOKING_NOT_FOUND", "Бронирование не найдено"));
        } catch (BookingAlreadyConfirmedException e) {
            return ResponseEntity.status(409).body(new ErrorResponse("BOOKING_ALREADY_CONFIRMED", "Бронирование уже подтверждено"));
        } catch (BookingExpiredException e) {
            return ResponseEntity.status(400).body(new ErrorResponse("BOOKING_EXPIRED", "Время бронирования истекло"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookingAdmin(
            @PathVariable Long id) {
        try {
            bookingService.deleteBookingAdmin(id);
            return ResponseEntity.noContent().build();
        } catch (BookingNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("BOOKING_NOT_FOUND", "Бронирование не найдено"));
        }
    }
}
