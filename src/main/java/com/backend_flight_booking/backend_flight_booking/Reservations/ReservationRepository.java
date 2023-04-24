package com.backend_flight_booking.backend_flight_booking.Reservations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationModel, Long> {

    public abstract Optional<ReservationModel> findByReservationCode(String reservationCode);

    public abstract Optional<ReservationModel> findByReservationCodeAndUserId(String reservationCode, Long id);
}
