import com.backend_flight_booking.backend_flight_booking.Exceptions.FlightIdNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Flights.Enums.FlightStatus;
import com.backend_flight_booking.backend_flight_booking.Flights.FlightModel;
import com.backend_flight_booking.backend_flight_booking.Flights.FlightRepository;
import com.backend_flight_booking.backend_flight_booking.Flights.FlightService;
import com.backend_flight_booking.backend_flight_booking.Seats.SeatModel;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = FlightServiceTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FlightServiceTests {

    @Spy
    @InjectMocks
    FlightService flightService;

    @Mock
    FlightRepository flightRepository;

    public FlightModel setupFlightModelToTest(Long id){

        FlightModel flightTest = new FlightModel();
        flightTest.setId(id);
        flightTest.setAirline("airline");
        flightTest.setDepartureDate(LocalDate.of(2023, 04, 18));
        flightTest.setDepartureTime(LocalTime.of(10, 30, 00));
        flightTest.setArrivalDate(LocalDate.of(2023, 04, 18));
        flightTest.setArrivalTime(LocalTime.of(12, 30, 00));
        flightTest.setOriginAirportCode("MDZ");
        flightTest.setDestinationAirportCode("AEP");

        return flightTest;
    }

    @BeforeClass
    public static void setupDateNow(){
        String instantExpected = String.valueOf(LocalDate.of(2023, 4, 8));
        LocalDate localDate = LocalDate.parse(instantExpected);
        MockedStatic<LocalDate> mockedLocalDateTime = Mockito.mockStatic(LocalDate.class);
        mockedLocalDateTime.when(LocalDate::now).thenReturn(localDate);
    }


    @Test
    public void testCreateFlightReturnCorrectUriStatusCodeFlightCode() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/flights");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        FlightModel flightModel = this.setupFlightModelToTest(1L);

        doReturn("AIRLINE-AXFRG").when(flightService).createFlightCode("airline");

        URI uri = flightService.createFlight(flightModel);

        URI expectedUri = URI.create("http://localhost/flights/" + flightModel.getId());
        assertEquals(expectedUri, uri);
        assertEquals(FlightStatus.NO_SEATS_CONFIGURATION, flightModel.getFlightStatus());
        assertEquals("AIRLINE-AXFRG", flightModel.getFlightCode());
    }

    @ParameterizedTest
    @EnumSource(FlightStatus.class)
    public void testAllFlightsByStatus(FlightStatus flightStatus) {
        Integer pageNum = 0;
        Integer pageSize = 10;
        List<FlightModel> expectedFlights = Arrays.asList(new FlightModel(), new FlightModel());
        Page<FlightModel> expectedPage = new PageImpl<>(expectedFlights);

        when(flightRepository.findFlightsByFlightStatus(any(FlightStatus.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<FlightModel> actualPage = flightService.allFlightsByStatus(pageNum, pageSize, flightStatus.toString());

        assertEquals(expectedPage, actualPage);
    }

    @Test
    public void testAllFlights() {
        Integer pageNum = 0;
        Integer pageSize = 10;
        Pageable paging = PageRequest.of(pageNum, pageSize);
        List<FlightModel> expectedFlights = Arrays.asList(new FlightModel(), new FlightModel());
        Page<FlightModel> expectedPage = new PageImpl<>(expectedFlights);

        when(flightRepository.findAll(paging))
                .thenReturn(expectedPage);

        Page<FlightModel> actualPage = flightService.allFlights(pageNum, pageSize);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    public void testGetFlightByIdWithInvalidId() {
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());
        FlightIdNotFoundException exception = assertThrows(
                FlightIdNotFoundException.class, () ->
                        flightService.flightById(1L));
        assertEquals("Flight with ID 1 not found", exception.getMessage());
    }

    @Test
    public void testGetFlightById() throws FlightIdNotFoundException {
        FlightModel flightModel = this.setupFlightModelToTest(1l);

        Optional<FlightModel> flightModelOptional = Optional.of(flightModel);

        when(flightRepository.findById(1L)).thenReturn(flightModelOptional);
        flightService.flightById(flightModel.getId());
        verify(flightRepository).findById(flightModel.getId());
    }

    @Test
    public void testUpdateFlightWithInvalidId(){
        FlightModel flightModel = this.setupFlightModelToTest(1L);
        when(flightRepository.findById(flightModel.getId())).thenReturn(Optional.empty());
        FlightIdNotFoundException exception = assertThrows(
                FlightIdNotFoundException.class, () ->
                        flightService.updateFlight(flightModel));
        assertEquals("Flight with ID 1 not found", exception.getMessage());
    }

    @Test
    public void testUpdateFlightWithValidId() throws FlightIdNotFoundException {
        FlightModel flightModel = this.setupFlightModelToTest(1L);
        when(flightRepository.findById(flightModel.getId())).thenReturn(Optional.of(flightModel));

        flightService.updateFlight(flightModel);
        verify(flightRepository).findById(flightModel.getId());
    }

    @Test
    public void testAllFlightsByDepartureArrivalNoResult(){

        Integer pagNum = 0;
        Integer pagSize = 10;
        String originAirportCode = "MDZ";
        String destinationAirportCode = "MDQ";
        LocalDate departureDate = LocalDate.now();
        LocalDate arrivalDate = LocalDate.now();
        String status = "ACTIVE";
        FlightStatus flightStatus = FlightStatus.valueOf(status);
        Pageable paging = PageRequest.of(pagNum, pagSize);
        Page<FlightModel> expectedPage = new PageImpl<>(new ArrayList<>());

        when(flightRepository
                .findFlightsByDepartureArrivalOriginDestination(
                        originAirportCode,
                    destinationAirportCode,
                    departureDate,
                    arrivalDate,
                    flightStatus,
                    paging))
                .thenReturn(expectedPage);

        flightService.allFlightsByDepartureArrival(
                pagNum,
                pagSize,
                originAirportCode,
                destinationAirportCode,
                departureDate,
                arrivalDate,
                status);
        verify(flightRepository).findFlightsByDepartureArrivalOriginDestination(
                originAirportCode,
                destinationAirportCode,
                departureDate,
                arrivalDate,
                flightStatus,
                paging);

    }


    @Test
    public void testAllFlightsByDepartureArrival() {
        Integer pageNum = 0;
        Integer pageSize = 10;
        String originAirportCode = "MDZ";
        String destinationAirportCode = "AEP";
        String status = "ACTIVE";
        FlightStatus flightStatus = FlightStatus.valueOf(status);
        LocalDate departureDate = LocalDate.of(2023,04,18);
        LocalDate arrivalDate = LocalDate.of(2023,04,18);

        List<FlightModel> flightList = Arrays.asList(
                this.setupFlightModelToTest(1L),
                this.setupFlightModelToTest(2L),
                this.setupFlightModelToTest(3L)
        );

        Page<FlightModel> expectedPage = new PageImpl<>(flightList);

        when(flightRepository.findFlightsByDepartureArrivalOriginDestination(
                originAirportCode,
                destinationAirportCode,
                departureDate,
                arrivalDate,
                flightStatus,
                PageRequest.of(pageNum, pageSize)
        )).thenReturn(expectedPage);


        Page<FlightModel> resultPage = flightService.allFlightsByDepartureArrival(
                pageNum,
                pageSize,
                originAirportCode,
                destinationAirportCode,
                departureDate,
                arrivalDate,
                status
        );

        assertEquals(expectedPage.getTotalElements(), resultPage.getTotalElements());
        assertEquals(expectedPage.getNumber(), resultPage.getNumber());
        assertEquals(expectedPage.getSize(), resultPage.getSize());
        assertEquals(expectedPage.getContent().size(), resultPage.getContent().size());
        assertEquals(expectedPage.getContent().get(0).getFlightCode(), resultPage.getContent().get(0).getFlightCode());
        assertEquals(expectedPage.getContent().get(1).getFlightCode(), resultPage.getContent().get(1).getFlightCode());
        assertEquals(expectedPage.getContent().get(2).getFlightCode(), resultPage.getContent().get(2).getFlightCode());
    }

}










