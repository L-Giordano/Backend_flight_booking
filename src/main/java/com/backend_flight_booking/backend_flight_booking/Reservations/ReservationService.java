package com.backend_flight_booking.backend_flight_booking.Reservations;

import com.backend_flight_booking.backend_flight_booking.Exceptions.FlightIdNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Exceptions.OccupiedSeatException;
import com.backend_flight_booking.backend_flight_booking.Exceptions.ReservationNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Exceptions.SeatNoFoundException;
import com.backend_flight_booking.backend_flight_booking.Flights.FlightModel;
import com.backend_flight_booking.backend_flight_booking.Reservations.DTO.ReservationInfoDto;
import com.backend_flight_booking.backend_flight_booking.Seats.SeatModel;
import com.backend_flight_booking.backend_flight_booking.Seats.SeatService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    SeatService seatService;

    @Transactional
    public URI createReservation(ReservationModel reservationModel) throws SeatNoFoundException, OccupiedSeatException {

        reservationModel.setReservationStatus(ReservationStatus.ACTIVE);
        reservationModel.setBookingDate(ZonedDateTime.now());
        while (true){
            String reservationCode = this.createReservationCode();
            if (reservationRepository.findByReservationCode(reservationCode).isEmpty()){
                reservationModel.setReservationCode(reservationCode);
                break;
            }
        }
        seatService.reserveSeat(reservationModel.getSeat().getId());
        reservationRepository.save(reservationModel);

        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservationModel.getId())
                .toUri();
    }

    public String createReservationCode(){
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int randomValue = random.nextInt(26);
            char randomLetter = (char) (randomValue + 65);
            builder.append(randomLetter);
        }
        return String.valueOf(builder);
    }

    public ReservationInfoDto getReservationByCode(Long id, String reservationCode) throws ReservationNotFoundException {
        ReservationModel reservation = reservationRepository.findByReservationCodeAndUserId(reservationCode, id)
                .orElseThrow(() -> new ReservationNotFoundException(
                                String.format("Reservation %s not found for the id %s", reservationCode, id)));
        SeatModel seat = reservation.getSeat();
        FlightModel flight = seat.getFlight();

        ReservationInfoDto response = new ReservationInfoDto();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.map(flight, response);
        modelMapper.map(seat, response);
        modelMapper.map(reservation, response);

        return response;
    }

    @Transactional
    public void cancelReservation(Long id) throws ReservationNotFoundException, SeatNoFoundException {
        ReservationModel reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(
                        String.format("Reservation %s not found", id)));
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        seatService.vacateSeat(reservation.getSeat().getId());
        reservationRepository.save(reservation);
    }
}
