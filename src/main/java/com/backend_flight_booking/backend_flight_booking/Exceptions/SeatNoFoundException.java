package com.backend_flight_booking.backend_flight_booking.Exceptions;

public class SeatNoFoundException extends Exception {
    public SeatNoFoundException  (String errorMessage) {
        super(errorMessage);
    }
}
