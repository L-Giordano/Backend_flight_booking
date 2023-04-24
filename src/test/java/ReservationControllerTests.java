import com.backend_flight_booking.backend_flight_booking.Exceptions.*;
import com.backend_flight_booking.backend_flight_booking.Flights.FlightModel;
import com.backend_flight_booking.backend_flight_booking.Reservations.ReservationController;
import com.backend_flight_booking.backend_flight_booking.Reservations.ReservationModel;
import com.backend_flight_booking.backend_flight_booking.Reservations.ReservationService;
import com.backend_flight_booking.backend_flight_booking.Seats.SeatModel;
import com.backend_flight_booking.backend_flight_booking.Seats.SeatStatus;
import com.backend_flight_booking.backend_flight_booking.Seats.SeatType;
import com.backend_flight_booking.backend_flight_booking.Users.UserModel;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ReservationControllerTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ReservationControllerTests {

    @InjectMocks
    ReservationController reservationController;

    @Mock
    ReservationService reservationService;

    @BeforeClass
    public static void setupDateNow() {
        String instantExpected = String.valueOf(LocalDate.of(2023, 5, 8));
        LocalDate localDate = LocalDate.parse(instantExpected);
        MockedStatic<LocalDate> mockedLocalDateTime = Mockito.mockStatic(LocalDate.class);
        mockedLocalDateTime.when(LocalDate::now).thenReturn(localDate);
    }

    public UserModel setupUserToTest(Long id) {
        UserModel userTest = new UserModel();
        userTest.setId(id);
        userTest.setFirstName("User");
        userTest.setFirstName("User");
        userTest.setEmail(String.format("user%s@user.com", id));
        userTest.setPassword("1234");
        return userTest;
    }

    public FlightModel setupFlightToTest(Long id) throws FlightIdNotFoundException, InvalidSeatKeyPatternException {

        FlightModel flightTest = new FlightModel();
        flightTest.setId(id);
        flightTest.setAirline("airline");
        flightTest.setDepartureDate(LocalDate.of(2023, 04, 18));
        flightTest.setArrivalDate(LocalDate.of(2023, 04, 18));
        flightTest.setDepartureTime(LocalTime.of(10,30));
        flightTest.setArrivalTime(LocalTime.of(12,30));
        flightTest.setOriginAirportCode("MDZ");
        flightTest.setDestinationAirportCode("AEP");
        flightTest.setFlightCode("CODER");

        HashMap<String, ArrayList<String>> seats = new HashMap<>();
        seats.put("A1", new ArrayList<>(Arrays.asList("FIRST_CLASS", "100.50")));
        seats.put("A2", new ArrayList<>(Arrays.asList("BUSINESS", "85.50")));
        seats.put("A2", new ArrayList<>(Arrays.asList("ECONOMIC", "100.00")));

        SeatModel seat1 = this.createSeat(flightTest, "A", "1", 100.5F, SeatType.FIRST_CLASS);
        SeatModel seat2 = this.createSeat(flightTest, "B", "1", 80.5F, SeatType.BUSINESS);
        SeatModel seat3 = this.createSeat(flightTest, "C", "1", 60.5F, SeatType.ECONOMIC);

        List<SeatModel> seatList = Arrays.asList(seat1, seat2, seat3);

        flightTest.setSeats(seatList);

        return flightTest;
    }

    public SeatModel createSeat(FlightModel flight, String row, String col, Float price, SeatType seatType) {
        SeatModel seat = new SeatModel();
        seat.setRow(row);
        seat.setCol(col);
        seat.setSeatStatus(SeatStatus.EMPTY);
        seat.setPrice(price);
        seat.setSeatType(seatType);
        seat.setStatus(true);
        seat.setFlight(flight);

        return seat;

    }

    public ReservationModel setupReservation(Long id) throws FlightIdNotFoundException, InvalidSeatKeyPatternException {

        FlightModel flight = this.setupFlightToTest(1L);
        SeatModel seat = flight.getSeats().get(0);
        UserModel user = this.setupUserToTest(1L);
        ReservationModel reservation = new ReservationModel();
        reservation.setId(id);
        reservation.setUser(user);
        reservation.setSeat(seat);
        reservation.setReservationCode("AXFTR");

        return reservation;
    }


    @Test
    public void testWhenPostReservationSuccess() throws
            FlightIdNotFoundException,
            InvalidSeatKeyPatternException,
            SeatNoFoundException,
            OccupiedSeatException {

        ReservationModel reservationModel = this.setupReservation(1L);
        URI uri = URI.create(String.format("localHost:8080/reservations/%s", reservationModel.getId()));

        when(reservationService.createReservation(reservationModel)).thenReturn(uri);

        ResponseEntity<Object> expected = ResponseEntity.created(uri).build();
        ResponseEntity<Object> result = reservationController.postReservation(reservationModel);

        assertEquals(expected, result);
    }

    @Test
    public void testWhenCreateReservationExceptionReturnsInternalServerError() throws
            FlightIdNotFoundException,
            InvalidSeatKeyPatternException,
            SeatNoFoundException,
            OccupiedSeatException {

        ReservationModel reservationModel = this.setupReservation(1L);

        when(reservationService.createReservation(reservationModel)).thenThrow(new IllegalArgumentException());

        ResponseEntity<Object> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        ResponseEntity<Object> result = reservationController.postReservation(reservationModel);

        assertEquals(expected, result);
    }

    @Test
    public void testWhenGetReservationByCodeNoFound() throws ReservationNotFoundException {
        Long id = 1L;
        String reservationCode = "ADGGH";

        when(reservationService.getReservationByCode(id, reservationCode))
                .thenThrow(ReservationNotFoundException.class);

        ResponseEntity<Object> expected = ResponseEntity.notFound().build();
        ResponseEntity<Object> result = reservationController.getReservationByCode(reservationCode, id);

        assertEquals(expected, result);

    }

    @Test
    public void testWhenUpdateReservationGetExceptionReturnsInternalServerError() throws
            SeatNoFoundException,
            ReservationNotFoundException {

        Long id = 1L;

        doThrow(new IllegalArgumentException()).when(reservationService).cancelReservation(id);
        ResponseEntity<Object> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        ResponseEntity<Object> result = reservationController.cancelReservation(id);

        assertEquals(expected, result);
    }

    @Test
    public void testWhenUpdateReservationWithInvalidId() throws
            SeatNoFoundException,
            ReservationNotFoundException {

        Long id = 1L;

        doThrow(new ReservationNotFoundException(String.format("Reservation %s not found", id)))
                .when(reservationService).cancelReservation(id);
        ResponseEntity<Object> expected = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(String.format("Reservation %s not found", id));
        ResponseEntity<Object> result = reservationController.cancelReservation(id);

        assertEquals(expected, result);
    }


}
