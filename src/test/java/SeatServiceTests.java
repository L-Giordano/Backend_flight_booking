import com.backend_flight_booking.backend_flight_booking.Exceptions.FlightIdNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Exceptions.InvalidSeatKeyPatternException;
import com.backend_flight_booking.backend_flight_booking.Exceptions.OccupiedSeatException;
import com.backend_flight_booking.backend_flight_booking.Exceptions.SeatNoFoundException;
import com.backend_flight_booking.backend_flight_booking.Flights.Enums.FlightStatus;
import com.backend_flight_booking.backend_flight_booking.Flights.FlightModel;
import com.backend_flight_booking.backend_flight_booking.Flights.FlightService;
import com.backend_flight_booking.backend_flight_booking.Seats.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = SeatServiceTests.class)
@RunWith(MockitoJUnitRunner.class)
public class SeatServiceTests {

    @InjectMocks
    SeatService seatService;

    @Mock
    SeatRepository seatRepository;

    @Mock
    FlightService flightService;

    public SeatModel setupSeat(Long id){
        SeatModel seatModel = new SeatModel();
        seatModel.setSeatStatus(SeatStatus.EMPTY);
        seatModel.setSeatType(SeatType.ECONOMIC);
        seatModel.setPrice(80.50F);
        seatModel.setCol("1");
        seatModel.setRow("A");

        return seatModel;
    }



    @Test
    public void testReserveSeatByIdWithInvalidId() {

        SeatModel seatModel = this.setupSeat(1L);
        when(seatRepository.findById(seatModel.getId())).thenReturn(Optional.empty());
        SeatNoFoundException exception = assertThrows(
                SeatNoFoundException.class, () ->
                        seatService.reserveSeat(1L));
        assertEquals("Seat with ID 1 not found", exception.getMessage());
    }

    @Test
    public void testReserveOccupiedSeat() {

        SeatModel seatModel = this.setupSeat(1L);
        seatModel.setSeatStatus(SeatStatus.RESERVED);
        when(seatRepository.findById(seatModel.getId())).thenReturn(Optional.of(seatModel));
        OccupiedSeatException exception = assertThrows(
                OccupiedSeatException.class, () ->
                        seatService.reserveSeat(seatModel.getId()));
        assertEquals(String.format(
                "The seat %s%s is occupied or blocked", seatModel.getRow(), seatModel.getCol()), exception.getMessage());
    }

    @Test
    public void testReserveSeatSuccess() throws SeatNoFoundException, OccupiedSeatException {

        SeatModel seatModel = this.setupSeat(1L);
        when(seatRepository.findById(seatModel.getId())).thenReturn(Optional.of(seatModel));
        seatService.reserveSeat(seatModel.getId());

        assertEquals(SeatStatus.RESERVED, seatModel.getSeatStatus());
        verify(seatRepository).save(seatModel);
    }

    @Test
    public void testVacateSeatByIdWithInvalidId() {

        SeatModel seatModel = this.setupSeat(1L);
        when(seatRepository.findById(seatModel.getId())).thenReturn(Optional.empty());
        SeatNoFoundException exception = assertThrows(
                SeatNoFoundException.class, () ->
                        seatService.vacateSeat(1L));
        assertEquals("Seat with ID 1 not found", exception.getMessage());
    }

    @Test
    public void testVacateSeatSuccess() throws SeatNoFoundException, OccupiedSeatException {

        SeatModel seatModel = this.setupSeat(1L);
        when(seatRepository.findById(seatModel.getId())).thenReturn(Optional.of(seatModel));
        seatService.vacateSeat(seatModel.getId());

        assertEquals(SeatStatus.EMPTY, seatModel.getSeatStatus());
        verify(seatRepository).save(seatModel);

    }


    @Test
    public void testCreateSeatsSuccess() throws FlightIdNotFoundException, InvalidSeatKeyPatternException {

        FlightModel flightModel = new FlightModel();
        flightModel.setId(1L);
        flightModel.setFlightStatus(FlightStatus.NO_SEATS_CONFIGURATION);

        HashMap<String, ArrayList<String>> seats = new HashMap<>();

        seats.put("A1", new ArrayList<>(Arrays.asList("FIRST_CLASS", "100.50")));
        seats.put("A2", new ArrayList<>(Arrays.asList("FIRST_CLASS", "100.50")));
        seats.put("A3", new ArrayList<>(Arrays.asList("FIRST_CLASS", "100.50")));

        when(flightService.flightById(flightModel.getId())).thenReturn(flightModel);

        seatService.createSeats(flightModel.getId(), seats);

        verify(seatRepository, times(3)).save(any(SeatModel.class));
        verify(flightService, times(1)).setStatusActive(flightModel);
    }

    @Test
    public void testCreateSeatsWithInvalidKeyPattern() throws FlightIdNotFoundException, InvalidSeatKeyPatternException {

        FlightModel flightModel = new FlightModel();
        flightModel.setId(1L);
        flightModel.setFlightStatus(FlightStatus.NO_SEATS_CONFIGURATION);

        HashMap<String, ArrayList<String>> seats = new HashMap<>();

        seats.put("111-33-AA", new ArrayList<>(Arrays.asList("FIRST_CLASS", "100.50")));

        when(flightService.flightById(flightModel.getId())).thenReturn(flightModel);

        InvalidSeatKeyPatternException exception = assertThrows(
                InvalidSeatKeyPatternException.class, () ->
                        seatService.createSeats(flightModel.getId(), seats));

        assertEquals("The key pattern is invalid. It must be ^[A-Z]+-\\d+-[A-Z\\s]+$", exception.getMessage());

    }

}
