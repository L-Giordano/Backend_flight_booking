package com.backend_flight_booking.backend_flight_booking.Flights;

import com.backend_flight_booking.backend_flight_booking.Flights.Enums.FlightStatus;
import com.backend_flight_booking.backend_flight_booking.Seats.SeatModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flight")
@Getter
@Setter
@EqualsAndHashCode
public class FlightModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String airline;

    @Column(nullable = false)
    private String flightCode;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatModel> seats = new ArrayList<>();

    @Column(nullable = false)
    private LocalDate departureDate;

    @Column(nullable = false)
    private LocalTime departureTime;

    @Column(nullable = false)
    private LocalDate arrivalDate;

    @Column(nullable = false)
    private LocalTime arrivalTime;

    @Column(nullable = false)
    private String originAirportCode;

    @Column(nullable = false)
    private String destinationAirportCode;

    @Column(nullable = false)
    private FlightStatus flightStatus;

}
