package com.mily.reservation;

import com.mily.user.LawyerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByLawyerUserAndReservationTimeBetween(LawyerUser lawyerUser, LocalDateTime startTime, LocalDateTime endTime);

    List<Reservation> findByLawyerUserIdAndReservationTime(Long lawyerUserId, LocalDateTime dateTime);

    Optional<Reservation> findByReservationTime(LocalDateTime reservationTime);

    List<Reservation> findByMilyUserId(long id);
}