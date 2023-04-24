package com.backend_flight_booking.backend_flight_booking.Exceptions;

public class ReservationNotFoundException extends Exception{
    public ReservationNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
