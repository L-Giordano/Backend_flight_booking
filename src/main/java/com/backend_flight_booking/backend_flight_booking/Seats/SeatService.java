package com.backend_flight_booking.backend_flight_booking.Seats;

import com.backend_flight_booking.backend_flight_booking.Exceptions.FlightIdNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Exceptions.InvalidSeatKeyPatternException;
import com.backend_flight_booking.backend_flight_booking.Exceptions.OccupiedSeatException;
import com.backend_flight_booking.backend_flight_booking.Exceptions.SeatNoFoundException;
import com.backend_flight_booking.backend_flight_booking.Flights.FlightModel;
import com.backend_flight_booking.backend_flight_booking.Flights.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SeatService {

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    FlightService flightService;


    @Transactional
    public void reserveSeat(Long seatId) throws OccupiedSeatException, SeatNoFoundException {
        SeatModel seatModel = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNoFoundException("Seat with ID " + seatId + " not found"));
        if(seatModel.getSeatStatus() != SeatStatus.EMPTY){
            throw new OccupiedSeatException(String.format(
                    "The seat %s%s is occupied or blocked", seatModel.getRow(), seatModel.getCol()));
        }
        seatModel.setSeatStatus(SeatStatus.RESERVED);
        seatRepository.save(seatModel);
    }

    @Transactional
    public void vacateSeat(Long seatId) throws SeatNoFoundException {
        SeatModel seatModel = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNoFoundException("Seat with ID " + seatId + " not found"));
        seatModel.setSeatStatus(SeatStatus.EMPTY);
        seatRepository.save(seatModel);
    }

    @Transactional
    public void createSeats(Long flightId, HashMap<String, ArrayList<String>> seats) throws FlightIdNotFoundException, InvalidSeatKeyPatternException {

        FlightModel flightModel = flightService.flightById(flightId);

        for (Map.Entry<String, ArrayList<String>> entry : seats.entrySet()) {
            String seatPosition = entry.getKey();
            if(!isValidateKeyPattern(seatPosition)){
                throw new InvalidSeatKeyPatternException("The key pattern is invalid. It must be ^[A-Z]+-\\d+-[A-Z\\s]+$");
            }
            Float price = Float.parseFloat(entry.getValue().get(1));
            SeatType seatType = SeatType.valueOf(entry.getValue().get(0));
            String col = seatPosition.replaceAll("[^\\d.]", "");
            String row = seatPosition.replaceAll("[\\d.]", "");

            SeatModel seat = new SeatModel();
            seat.setCol(col);
            seat.setRow(row);
            seat.setFlight(flightModel);
            seat.setSeatType(seatType);
            seat.setSeatStatus(SeatStatus.EMPTY);
            seat.setStatus(true);
            seat.setPrice(price);
            seatRepository.save(seat);
        }
        flightService.setStatusActive(flightModel);
    }


    public Boolean isValidateKeyPattern(String key){
        String pattern = "^[A-Z]+\\d+$";
        return key.matches(pattern);
    }

    public List<SeatModel> getSeatsByFlightId(Long flightId){

        return seatRepository.findSeatsByFlightId(flightId);



    }



}
