package com.backend_flight_booking.backend_flight_booking.Seats.DTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class SeatToCreateDTO {

    private Long flightId;
    private HashMap<String, ArrayList<String>> seatList;

}
