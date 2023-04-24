package com.backend_flight_booking.backend_flight_booking.Reservations.DTO;

import com.backend_flight_booking.backend_flight_booking.Reservations.ReservationStatus;
import com.backend_flight_booking.backend_flight_booking.Seats.SeatType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Data
public class ReservationInfoDto {
    private String airline;
    private String flightCode;
    private LocalDate departureDate;
    private LocalDate arrivalDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String originAirportCode;
    private String destinationAirportCode;
    private String row;
    private String col;
    private SeatType seatType;
    private String reservationCode;
    private ReservationStatus reservationStatus;


}
