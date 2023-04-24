package com.backend_flight_booking.backend_flight_booking.Seats;

import com.backend_flight_booking.backend_flight_booking.Flights.FlightModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<SeatModel, Long> {

    @Query(value= "SELECT s FROM SeatModel s WHERE " +
            "s.flight.id = :flightId ")
    List<SeatModel> findSeatsByFlightId(
            @Param("flightId") Long flightId
    );
}
