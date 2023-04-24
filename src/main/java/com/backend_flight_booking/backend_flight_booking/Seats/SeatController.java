package com.backend_flight_booking.backend_flight_booking.Seats;

import com.backend_flight_booking.backend_flight_booking.Exceptions.FlightIdNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Exceptions.InvalidSeatKeyPatternException;
import com.backend_flight_booking.backend_flight_booking.Seats.DTO.SeatToCreateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/seats")
public class SeatController {

    @Autowired
    SeatService seatService;

    @PostMapping("/createSeats")
    public ResponseEntity<Object> createSeats(@RequestBody SeatToCreateDTO seatToCreateDTO) {
        Long flightId = seatToCreateDTO.getFlightId();
        HashMap<String, ArrayList<String>> seatList = seatToCreateDTO.getSeatList();

        try {
            seatService.createSeats(flightId, seatList);
            return ResponseEntity.noContent().build();
        } catch (FlightIdNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (InvalidSeatKeyPatternException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/seatsByFlight")
    public ResponseEntity<Object> getSeatsByFlightId(@RequestParam(value = "flightId") Long flightId){
        List<SeatModel> response = seatService.getSeatsByFlightId(flightId);
        return ResponseEntity.ok(response);
    }




}