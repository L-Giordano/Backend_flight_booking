import com.backend_flight_booking.backend_flight_booking.Exceptions.FlightIdNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Exceptions.UserIdNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Flights.Enums.FlightStatus;
import com.backend_flight_booking.backend_flight_booking.Flights.FlightController;
import com.backend_flight_booking.backend_flight_booking.Flights.FlightModel;
import com.backend_flight_booking.backend_flight_booking.Flights.FlightService;
import com.backend_flight_booking.backend_flight_booking.Users.DTO.UserBasicInfoDTO;
import com.backend_flight_booking.backend_flight_booking.Users.UserModel;
import com.backend_flight_booking.backend_flight_booking.Users.UserRepository;
import com.backend_flight_booking.backend_flight_booking.Users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserServiceTests.class)
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTests {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    ModelMapper modelMapper;



    public UserModel setupUserModelToTest(Long id){
        UserModel userTest = new UserModel();
        userTest.setId(id);
        userTest.setFirstName("User");
        userTest.setFirstName("User");
        userTest.setEmail(String.format("user%s@user.com", id));
        userTest.setPassword("1234");
        return userTest;
    }

    @Test
    public void testCreateUserReturnCorrectUri() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UserModel user1 = this.setupUserModelToTest(1l);

        URI uri = userService.createUser(user1);

        URI expectedUri = URI.create("http://localhost/users/" + user1.getId());
        assertEquals(expectedUri, uri);
    }

    @Test
    public void testCreateUserSetUserStatusTrue(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        UserModel user1 = this.setupUserModelToTest(1l);
        userService.createUser(user1);
        assertTrue(user1.getStatus());
    }

    @Test
    public void testGetUserByIdWithInvalidId() throws UserIdNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserIdNotFoundException exception = assertThrows(
                UserIdNotFoundException.class, () ->
                userService.getUserById(1L));
        assertEquals("User with ID 1 not found", exception.getMessage());
    }

    @Test
    public void testGetUserById() throws UserIdNotFoundException {
        UserModel user = this.setupUserModelToTest(1l);
        user.setStatus(true);

        Optional<UserModel> userOptional = Optional.of(user);

        when(userRepository.findById(1L)).thenReturn(userOptional);
        userService.getUserById(user.getId());
        verify(modelMapper).map(user, UserBasicInfoDTO.class);
    }

    @Test
    public void testUpdateUserWithInvalidId() throws UserIdNotFoundException {
        UserModel user = this.setupUserModelToTest(1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        UserIdNotFoundException exception = assertThrows(
                UserIdNotFoundException.class, () ->
                        userService.updateUser(user));
        assertEquals("User Id 1 not Found", exception.getMessage());
    }

    @Test
    public void testUpdateUserWithValidId() throws UserIdNotFoundException {
        UserModel user = this.setupUserModelToTest(1l);
        Optional<UserModel> userOptional = Optional.of(user);

        when(userRepository.findById(user.getId())).thenReturn(userOptional);
        userService.updateUser(user);

        verify(userRepository, Mockito.times(1)).save(userOptional.get());
    }

    @Test
    public void testDeleteUserWithInvalidId() throws UserIdNotFoundException {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());
        UserIdNotFoundException exception = assertThrows(
                UserIdNotFoundException.class, () ->
                        userService.deleteUser(id));

        assertEquals("User Id 1 not Found", exception.getMessage());

    }

    @Test
    public void testDeleteUserChangeStatusToFalse() throws UserIdNotFoundException {
        UserModel user1 = this.setupUserModelToTest(1l);
        user1.setStatus(true);

        Optional<UserModel> userOptional = Optional.of(user1);

        when(userRepository.findById(1L)).thenReturn(userOptional);
        userService.deleteUser(user1.getId());

        assertFalse(user1.getStatus());
    }

    @SpringBootTest(classes = FlightControllerTests.class)
    @RunWith(MockitoJUnitRunner.class)
    public static class FlightControllerTests {

        @InjectMocks
        FlightController flightController;

        @Mock
        FlightService flightService;

        public FlightModel setupFlightModelForBody(Long id){
            FlightModel flightTest = new FlightModel();
            flightTest.setId(id);
            flightTest.setAirline("airline");
            flightTest.setDepartureDate(LocalDate.of(2023, 04, 18));
            flightTest.setDepartureTime(LocalTime.of(10, 30, 00));
            flightTest.setArrivalDate(LocalDate.of(2023, 04, 18));
            flightTest.setArrivalTime(LocalTime.of(12, 30, 00));
            flightTest.setOriginAirportCode("MDZ");
            flightTest.setDestinationAirportCode("AEP");
            flightTest.setFlightStatus(FlightStatus.NO_SEATS_CONFIGURATION);

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
        public void testWhenCreateUserSuccess(){

            FlightModel flightModel = this.setupFlightModelForBody(1L);
            URI uri = URI.create(String.format("localHost:8080/flights/%s", flightModel.getId()));

            when(flightService.createFlight(flightModel)).thenReturn(uri);

            ResponseEntity<Object> expected = ResponseEntity.created(uri).build();
            ResponseEntity<Object> result = flightController.createFlight(flightModel);

            assertEquals(expected, result);
        }

        @Test
        public void testWhenCreateFlightGetExceptionReturnsInternalServerError(){

            FlightModel flightModel = this.setupFlightModelForBody(1L);

            when(flightService.createFlight(flightModel)).thenThrow(new IllegalArgumentException());

            ResponseEntity<Object> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            ResponseEntity<Object> result = flightController.createFlight(flightModel);

            assertEquals(expected, result);
        }

        @Test
        public void testWhenGetUserByIdSuccess() throws FlightIdNotFoundException {
            Long id = 1L;
            FlightModel flightModel = this.setupFlightModelForBody(1L);

            when(flightService.flightById(id)).thenReturn(flightModel);

            ResponseEntity<Object> expected = ResponseEntity.ok(flightModel);
            ResponseEntity<Object> result = flightController.getFlight(id);

            assertEquals(expected, result);
        }

        @Test
        public void testWhenGetFlightByIdNoFound() throws UserIdNotFoundException, FlightIdNotFoundException {
            Long id = 1L;

            when(flightService.flightById(id)).thenThrow(FlightIdNotFoundException.class);

            ResponseEntity<Object> expected = ResponseEntity.badRequest().build();
            ResponseEntity<Object> result = flightController.getFlight(id);

            assertEquals(expected, result);

        }

        @Test
        public void testWhenGetFlightByIdHasInternalServerError() throws FlightIdNotFoundException {
            Long id = 1L;

            when(flightService.flightById(id)).thenThrow(IllegalArgumentException.class);

            ResponseEntity<Object> expected = ResponseEntity.internalServerError().build();
            ResponseEntity<Object> result = flightController.getFlight(id);

            assertEquals(expected, result);

        }

        @Test
        public void testWhenUpdateFlightByIdUserNotFound() throws FlightIdNotFoundException {
            FlightModel flightModel = this.setupFlightModelForBody(1L);

            doThrow(
                    new FlightIdNotFoundException(
                            String.format("Flight Id %s not Found", flightModel.getId().toString())))
                    .when(flightService).updateFlight(flightModel);

            ResponseEntity<Object> expected = ResponseEntity
                    .badRequest()
                    .body(String.format("Flight Id %s not Found", flightModel.getId().toString()));
            ResponseEntity<Object> result = flightController.updateFlight(flightModel);

            assertEquals(expected, result);
        }

        @Test
        public void testWhenUpdateFlightByIdHasInternalServerError() throws FlightIdNotFoundException {
            FlightModel flightModel = this.setupFlightModelForBody(1L);

            doThrow(
                    new IllegalArgumentException())
                    .when(flightService).updateFlight(flightModel);

            ResponseEntity<Object> expected = ResponseEntity.internalServerError().build();
            ResponseEntity<Object> result = flightController.updateFlight(flightModel);

            assertEquals(expected, result);
        }

        @Test
        public void testWhenUpdateFlightByIdSuccess() throws  FlightIdNotFoundException {
            FlightModel flightModel = this.setupFlightModelForBody(1L);

            doNothing().when(flightService).updateFlight(flightModel);

            ResponseEntity<Object> expected = ResponseEntity.noContent().build();
            ResponseEntity<Object> result = flightController.updateFlight(flightModel);

            assertEquals(expected, result);
        }

        @Test
        public void testWhenGetFlightsByDepartureAndArrivalHasInternalServerError() {

            Integer pagNum = 0;
            Integer pagSize = 10;
            String originAirportCode = "MDZ";
            String destinationAirportCode = "MDQ";
            LocalDate departureDate = LocalDate.now();
            LocalDate arrivalDate = LocalDate.now();
            String status = "ACTIVE";

            doThrow(
                    new IllegalArgumentException())
                    .when(flightService)
                    .allFlightsByDepartureArrival(
                            pagNum, pagSize, originAirportCode, destinationAirportCode, departureDate, arrivalDate, status);

            ResponseEntity<Object> expected = ResponseEntity.internalServerError().build();
            ResponseEntity<Object> result = flightController
                    .getFlightsByDepartureAndArrival(
                            arrivalDate, departureDate, destinationAirportCode, originAirportCode, pagSize, pagNum, status);

            assertEquals(expected, result);

        }
            @Test
            public void testWhenGetFlightsByDepartureAndArrivalSuccess(){

                FlightModel flightModel = this.setupFlightModelForBody(1L);

                Integer pagNum = 0;
                Integer pagSize = 10;
                String originAirportCode = "MDZ";
                String destinationAirportCode = "MDQ";
                LocalDate departureDate = LocalDate.now();
                LocalDate arrivalDate = LocalDate.now();
                String status = "ACTIVE";
                Page<FlightModel> expectedPage = new PageImpl<>(Arrays.asList(flightModel));

                when(flightService
                        .allFlightsByDepartureArrival(
                                pagNum, pagSize, originAirportCode, destinationAirportCode, departureDate, arrivalDate,status))
                        .thenReturn(expectedPage);

                ResponseEntity<Object> expected = ResponseEntity.ok().body(expectedPage);
                ResponseEntity<Object> result = flightController
                        .getFlightsByDepartureAndArrival(
                                arrivalDate,departureDate,destinationAirportCode, originAirportCode, pagSize, pagNum, status);

                assertEquals(expected, result);

        }

    }
}
