package com.backend_flight_booking.backend_flight_booking.Exceptions;

public class FlightIdNotFoundException extends Exception{
    public FlightIdNotFoundException  (String errorMessage) {
        super(errorMessage);
    }
}
