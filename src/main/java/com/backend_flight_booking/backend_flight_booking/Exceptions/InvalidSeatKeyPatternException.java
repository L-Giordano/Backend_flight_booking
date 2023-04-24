package com.backend_flight_booking.backend_flight_booking.Exceptions;

public class InvalidSeatKeyPatternException extends Exception{
    public InvalidSeatKeyPatternException  (String errorMessage) {
        super(errorMessage);
    }

}
