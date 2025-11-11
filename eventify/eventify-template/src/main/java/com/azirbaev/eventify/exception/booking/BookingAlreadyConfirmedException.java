package com.azirbaev.eventify.exception.booking;

public class BookingAlreadyConfirmedException extends RuntimeException {
    public BookingAlreadyConfirmedException(String message) {
        super(message);
    }
}
