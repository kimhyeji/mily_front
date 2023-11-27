package com.mily.reservation;

import com.mily.user.LawyerUser;
import com.mily.user.MilyUser;
import com.mily.user.MilyUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MilyUserService milyUserService;

    // 예약 저장
    public void saveReservation(MilyUser milyUser, LawyerUser lawyerUser, LocalDateTime time) {
        Reservation reservation = new Reservation();
        reservation.setMilyUser(milyUser);
        reservation.setLawyerUser(lawyerUser);
        reservation.setReservationTime(time);
        reservationRepository.save(reservation);
    }

    // 예약 거절
    public void refuseReservation(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    // 예약 가능한 시간 표시
    public List<LocalDateTime> getAvailableTimes(Long lawyerUserId, LocalDate reservationTime) {
        List<LocalDateTime> availableTimes = new ArrayList<>();
        LocalDateTime dateTime;
        for (int hour = 9; hour < 18; hour++) {
            if (hour != 12) {
                dateTime = reservationTime.atTime(hour, 0);
                List<Reservation> findAvailableTimes = reservationRepository.findByLawyerUserIdAndReservationTime(lawyerUserId, dateTime);
                if (findAvailableTimes.isEmpty()) {
                    availableTimes.add(dateTime);
                }
            }
        }

        return availableTimes;
    }

    public Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow();
    }

    public Optional<Reservation> findByReservationTime(LocalDateTime reservationTime) {
        return reservationRepository.findByReservationTime(reservationTime);
    }

    public List<Reservation> findByMilyUser(MilyUser milyUser) {
        return reservationRepository.findByMilyUserId(milyUser.getId());
    }
}