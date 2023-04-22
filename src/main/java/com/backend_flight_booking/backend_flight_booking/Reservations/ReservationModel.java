package com.backend_flight_booking.backend_flight_booking.Reservations;

import com.backend_flight_booking.backend_flight_booking.Seats.SeatModel;
import com.backend_flight_booking.backend_flight_booking.Users.UserModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@EqualsAndHashCode
public class ReservationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private SeatModel seat;

    @Column
    private String reservationCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @Column(nullable = false)
    private ZonedDateTime bookingDate;

    @Column(nullable = false)
    private Boolean status;
}
