package com.azirbaev.eventify.mapper;

import com.azirbaev.eventify.dto.event.EventCreateRequest;
import com.azirbaev.eventify.dto.event.EventResponse;
import com.azirbaev.eventify.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventResponse toEventResponse(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "availableTickets", ignore = true)
    Event toEvent(EventCreateRequest request);
}