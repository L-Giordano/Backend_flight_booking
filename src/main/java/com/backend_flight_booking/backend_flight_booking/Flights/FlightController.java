package com.backend_flight_booking.backend_flight_booking.Flights;

import com.backend_flight_booking.backend_flight_booking.Exceptions.FlightIdNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Seats.SeatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    @Autowired
    FlightService flightService;

    @PostMapping
    public ResponseEntity<Object> createFlight(@RequestBody FlightModel flightModel){
        try {
            URI uri = flightService.createFlight(flightModel);
            return ResponseEntity.created(uri).build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFlight(@PathVariable Long id) {
        try {
            FlightModel flightModel = flightService.flightById(id);
            return ResponseEntity.ok(flightModel);
        }catch (FlightIdNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<Object> updateFlight(@RequestBody FlightModel flightModel){
        try {
            flightService.updateFlight(flightModel);
            return ResponseEntity.noContent().build();
        }catch (FlightIdNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/byDepartureAndArrival")
    public ResponseEntity<Object> getFlightsByDepartureAndArrival(
            @RequestParam("arrivalDate") LocalDate arrival,
            @RequestParam("departureDate")LocalDate departure,
            @RequestParam("destinationAirportCode") String destinationAirportCode,
            @RequestParam("originAirportCode") String originAirportCode,
            @RequestParam(value = "pagSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pagNum", required = false, defaultValue = "0") Integer pageNum,
            @RequestParam(value = "flightStatus", required = false, defaultValue = "ACTIVE") String flightStatus
    )
    {
        try {
            Page<FlightModel> response = flightService.allFlightsByDepartureArrival(
                    pageNum,
                    pageSize,
                    originAirportCode,
                    destinationAirportCode,
                    departure,
                    arrival,
                    flightStatus);
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @GetMapping("/flightsByStatus")
    public ResponseEntity<Object> getAllFlightByStatus(
            @RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "status") String status){
        try {
            Page<FlightModel> response = flightService.allFlightsByStatus(pageNum, pageSize, status);
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
