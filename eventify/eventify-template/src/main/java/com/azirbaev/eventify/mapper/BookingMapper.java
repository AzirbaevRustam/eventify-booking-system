package com.azirbaev.eventify.mapper;

import com.azirbaev.eventify.dto.booking.BookingResponse;
import com.azirbaev.eventify.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "user.email", target = "customerEmail")
    @Mapping(source = "event", target = "event")
    BookingResponse toBookingResponse(Booking booking);
}