package com.azirbaev.eventify.repository;

import com.azirbaev.eventify.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByEventId(Long eventId);

    List<Booking> findByConfirmed(Boolean confirmed);

    List<Booking> findByEventIdAndConfirmed(Long eventId, Boolean confirmed);
}
