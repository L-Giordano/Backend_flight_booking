package com.backend_flight_booking.backend_flight_booking.Exceptions;

public class OccupiedSeatException extends Exception{
    public OccupiedSeatException  (String errorMessage) {
        super(errorMessage);
    }
}
