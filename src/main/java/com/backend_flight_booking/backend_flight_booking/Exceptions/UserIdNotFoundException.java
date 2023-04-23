package com.backend_flight_booking.backend_flight_booking.Exceptions;

public class UserIdNotFoundException extends Exception{
    public UserIdNotFoundException  (String errorMessage) {
        super(errorMessage);
    }
}
