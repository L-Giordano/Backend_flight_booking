package com.backend_flight_booking.backend_flight_booking.Flights;

import com.backend_flight_booking.backend_flight_booking.Exceptions.FlightIdNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Flights.Enums.FlightStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;

@Service
public class FlightService {

    @Autowired
    FlightRepository flightRepository;

    @Transactional
    public URI createFlight(FlightModel flightModel){
        flightModel.setFlightStatus(FlightStatus.NO_SEATS_CONFIGURATION);
        flightModel.setFlightCode(this.createFlightCode(flightModel.getAirline()));
        flightRepository.save(flightModel);
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(flightModel.getId())
                .toUri();
    }

    public String createFlightCode(String airline){
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int randomValue = random.nextInt(26);
            char randomLetter = (char) (randomValue + 65);
            builder.append(randomLetter);
        }

        String airlineName = airline.replaceAll(" ","").toUpperCase();
        return airlineName +"-"+ builder;
    }

    public Page<FlightModel> allFlightsByStatus(Integer pageNum, Integer pageSize, String status){
        FlightStatus flightStatus = FlightStatus.valueOf(status);
        Pageable paging = PageRequest.of(pageNum, pageSize);

        return flightRepository.findFlightsByFlightStatus(flightStatus, paging);
    }

    public Page<FlightModel> allFlights(Integer pageNum, Integer pageSize){
        Pageable paging = PageRequest.of(pageNum, pageSize);
        return flightRepository.findAll(paging);
    }

    public FlightModel flightById(Long id) throws FlightIdNotFoundException {
        return flightRepository.findById(id)
                .orElseThrow(() -> new FlightIdNotFoundException("Flight with ID " + id + " not found"));
    }

    @Transactional
    public void updateFlight(FlightModel flightModel) throws FlightIdNotFoundException {
        flightRepository
                .findById(flightModel.getId())
                .orElseThrow(() -> new FlightIdNotFoundException("Flight with ID " + flightModel.getId()+ " not found"));
        flightRepository.save(flightModel);
    }

    public Page<FlightModel> allFlightsByDepartureArrival(
            Integer pageNum,
            Integer pageSize,
            String originAirportCode,
            String destinationAirportCode,
            LocalDate departureDate,
            LocalDate arrivalDate,
            String status){
        FlightStatus flightStatus = FlightStatus.valueOf(status);
        Pageable paging = PageRequest.of(pageNum, pageSize);
        return flightRepository.findFlightsByDepartureArrivalOriginDestination(
                originAirportCode,
                destinationAirportCode,
                departureDate,
                arrivalDate,
                flightStatus,
                paging);
    }

    public void setStatusActive(FlightModel flightModel){
        flightModel.setFlightStatus(FlightStatus.ACTIVE);
        flightRepository.save(flightModel);
    }


}
