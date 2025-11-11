package com.azirbaev.eventify.service;

import com.azirbaev.eventify.dto.event.EventCreateRequest;
import com.azirbaev.eventify.dto.event.EventResponse;
import com.azirbaev.eventify.dto.event.EventUpdateRequest;
import com.azirbaev.eventify.entity.Event;
import com.azirbaev.eventify.entity.User;
import com.azirbaev.eventify.entity.UserStatus;
import com.azirbaev.eventify.exception.UserNotFoundException;
import com.azirbaev.eventify.exception.event.EventNotFoundException;
import com.azirbaev.eventify.exception.event.InvalidEventDateException;
import com.azirbaev.eventify.exception.event.InvalidTicketCountException;
import com.azirbaev.eventify.mapper.EventMapper;
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
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventResponse createEvent(EventCreateRequest request, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);

        if (currentUser.getRole() != UserStatus.ADMIN) {
            throw new AccessDeniedException("Только ADMIN может создавать мероприятия");
        }
        if (request.getDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidEventDateException("Дата мероприятия должна быть в будущем");
        }
        if (request.getTotalTickets() <= 0) {
            throw new InvalidTicketCountException("Количество билетов должно быть больше 0");
        }

        Event event = eventMapper.toEvent(request);
        event.setAvailableTickets(request.getTotalTickets());

        Event savedEvent = eventRepository.save(event);

        return eventMapper.toEventResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEvents() {
        List<Event> events = eventRepository.findByDateTimeAfter(LocalDateTime.now());
        return events.stream()
                .map(eventMapper::toEventResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        return eventMapper.toEventResponse(event);
    }

    @Transactional
    public EventResponse updateEvent(EventUpdateRequest request, Long currentUserId, Long eventId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);

        if (currentUser.getRole() != UserStatus.ADMIN) {
            throw new AccessDeniedException("Только ADMIN может обновлять мероприятия");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getDateTime() != null) {
            if (request.getDateTime().isBefore(LocalDateTime.now())) {
                throw new InvalidEventDateException("Дата мероприятия должна быть в будущем");
            }
            event.setDateTime(request.getDateTime());
        }
        if (request.getTotalTickets() != null) {
            if (request.getTotalTickets() <= 0) {
                throw new InvalidTicketCountException("Количество билетов должно быть больше 0");
            }
            int bookedTickets = event.getTotalTickets() - event.getAvailableTickets();
            event.setTotalTickets(request.getTotalTickets());
            event.setAvailableTickets(request.getTotalTickets() - bookedTickets);
        }
        if (request.getCoverUrl() != null) {
            event.setCoverUrl(request.getCoverUrl());
        }

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toEventResponse(updatedEvent);
    }

    @Transactional
    public void deleteEvent(Long currentUserId, Long eventId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);

        if (currentUser.getRole() != UserStatus.ADMIN) {
            throw new AccessDeniedException("Только ADMIN может обновлять мероприятия");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        eventRepository.delete(event);
    }
}