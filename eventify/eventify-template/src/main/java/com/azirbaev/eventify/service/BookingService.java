package com.azirbaev.eventify.service;

import com.azirbaev.eventify.dto.booking.BookingResponse;
import com.azirbaev.eventify.dto.booking.CreateBookingRequest;
import com.azirbaev.eventify.dto.booking.UpdateBookingRequest;
import com.azirbaev.eventify.entity.Booking;
import com.azirbaev.eventify.entity.Event;
import com.azirbaev.eventify.entity.User;
import com.azirbaev.eventify.entity.UserStatus;
import com.azirbaev.eventify.exception.UserNotFoundException;
import com.azirbaev.eventify.exception.booking.BookingAlreadyConfirmedException;
import com.azirbaev.eventify.exception.booking.BookingExpiredException;
import com.azirbaev.eventify.exception.booking.BookingNotFoundException;
import com.azirbaev.eventify.exception.booking.NotEnoughTicketsException;
import com.azirbaev.eventify.exception.event.EventNotFoundException;
import com.azirbaev.eventify.exception.event.InvalidEventDateException;
import com.azirbaev.eventify.exception.event.InvalidTicketCountException;
import com.azirbaev.eventify.mapper.BookingMapper;
import com.azirbaev.eventify.repository.BookingRepository;
import com.azirbaev.eventify.repository.EventRepository;
import com.azirbaev.eventify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request, Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(EventNotFoundException::new);

        if (event.getDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidEventDateException("Нельзя бронировать на прошедшие мероприятия");
        }
        if (event.getAvailableTickets() < request.getTicketCount()) {
            throw new NotEnoughTicketsException("Недостаточно доступных билетов");
        }

        if (request.getTicketCount() <= 0) {
            throw new InvalidTicketCountException("Количество билетов должно быть больше 0");
        }

        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setEvent(event);
        booking.setTicketCount(request.getTicketCount());
        booking.setConfirmed(false);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setExpiryTime(LocalDateTime.now().plusHours(24));

        event.setAvailableTickets(event.getAvailableTickets() - request.getTicketCount());
        eventRepository.save(event);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("User not found with ID: {}", userId);
            throw new UserNotFoundException();
        }

        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return bookings.stream()
                .map(bookingMapper::toBookingResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        if (!booking.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Доступ запрещен. Вы можете просматривать только свои бронирования");
        }

        return bookingMapper.toBookingResponse(booking);
    }

    @Transactional
    public BookingResponse updateBooking(Long bookingId, UpdateBookingRequest request, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        if (!booking.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Можно изменять только свои бронирования");
        }
        if (booking.getConfirmed()) {
            throw new BookingAlreadyConfirmedException("Нельзя изменять подтвержденное бронирование");
        }
        if (booking.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BookingExpiredException("Время бронирования истекло");
        }

        Event event = booking.getEvent();
        int oldTicketCount = booking.getTicketCount();
        int newTicketCount = request.getTicketCount();

        if (newTicketCount <= 0) {
            throw new InvalidTicketCountException("Количество билетов должно быть больше 0");
        }
        if (newTicketCount > oldTicketCount) {
            int additionalTickets = newTicketCount - oldTicketCount;
            if (event.getAvailableTickets() < additionalTickets) {
                throw new NotEnoughTicketsException("Недостаточно доступных билетов");
            }
        }

        int ticketDifference = newTicketCount - oldTicketCount;
        event.setAvailableTickets(event.getAvailableTickets() - ticketDifference);
        eventRepository.save(event);

        booking.setTicketCount(newTicketCount);
        Booking updatedBooking = bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(updatedBooking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        boolean isOwner = booking.getUser().getId().equals(userId);
        boolean isAdmin = currentUser.getRole() == UserStatus.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Можно отменять только свои бронирования");
        }

        Event event = booking.getEvent();
        event.setAvailableTickets(event.getAvailableTickets() + booking.getTicketCount());
        eventRepository.save(event);

        bookingRepository.delete(booking);

        log.info("Booking ID: {} canceled by user ID: {}", bookingId, userId);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings(Long eventId, Boolean unconfirmedOnly) {
        List<Booking> bookings;

        if (eventId != null && unconfirmedOnly != null && unconfirmedOnly) {
            bookings = bookingRepository.findByEventIdAndConfirmed(eventId, false);
        } else if (eventId != null) {
            bookings = bookingRepository.findByEventId(eventId);
        } else if (unconfirmedOnly != null && unconfirmedOnly) {
            bookings = bookingRepository.findByConfirmed(false);
        } else {
            bookings = bookingRepository.findAll();
        }

        return bookings.stream()
                .map(bookingMapper::toBookingResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void confirmBooking(Long bookingId) {
        log.info("Admin confirming booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        if (booking.getConfirmed()) {
            throw new BookingAlreadyConfirmedException("Бронирование уже подтверждено");
        }
        if (booking.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BookingExpiredException("Время бронирования истекло");
        }

        booking.setConfirmed(true);
        bookingRepository.save(booking);

        log.info("Booking ID: {} successfully confirmed", bookingId);
    }

    @Transactional
    public void deleteBookingAdmin(Long bookingId) {
        log.info("Admin deleting booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        if (!booking.getConfirmed()) {
            Event event = booking.getEvent();
            event.setAvailableTickets(event.getAvailableTickets() + booking.getTicketCount());
            eventRepository.save(event);
            log.info("Returned {} tickets to event ID: {}", booking.getTicketCount(), event.getId());
        }

        bookingRepository.delete(booking);
        log.info("Booking ID: {} deleted by admin. Was confirmed: {}", bookingId, booking.getConfirmed());
    }
}
