package com.azirbaev.eventify.exception.event;

public class InvalidTicketCountException extends RuntimeException {
    public InvalidTicketCountException(String message) {
        super(message);
    }
}
