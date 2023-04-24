package com.backend_flight_booking.backend_flight_booking.Flights;

import com.backend_flight_booking.backend_flight_booking.Flights.Enums.FlightStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface FlightRepository extends JpaRepository<FlightModel, Long> {
    public abstract Page<FlightModel> findFlightsByFlightStatus(FlightStatus flightStatus, Pageable pageable);

    @Query(value= "SELECT f FROM FlightModel f WHERE " +
            "f.originAirportCode = :originAirportCode " +
            "AND f.destinationAirportCode = :destinationAirportCode " +
            "AND f.departureDate = :departureDate " +
            "AND f.arrivalDate = :arrivalDate " +
            "AND f.flightStatus = :flightStatus")
    Page<FlightModel> findFlightsByDepartureArrivalOriginDestination(
            @Param("originAirportCode") String originAirportCode,
            @Param("destinationAirportCode") String destinationAirportCode,
            @Param("departureDate") LocalDate departureDate,
            @Param("arrivalDate") LocalDate arrivalDate,
            @Param("flightStatus") FlightStatus flightStatus,
            @Param("paging") Pageable pageable
    );
}
