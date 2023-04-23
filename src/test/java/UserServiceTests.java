import com.backend_flight_booking.backend_flight_booking.Exceptions.UserIdNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Users.DTO.UserBasicInfoDTO;
import com.backend_flight_booking.backend_flight_booking.Users.UserModel;
import com.backend_flight_booking.backend_flight_booking.Users.UserRepository;
import com.backend_flight_booking.backend_flight_booking.Users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void testGetActiveUserByIdWithInvalidId() throws UserIdNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserIdNotFoundException exception = assertThrows(
                UserIdNotFoundException.class, () ->
                userService.getActiveUserById(1L));
        assertEquals("User Id 1 not Found", exception.getMessage());
    }

    @Test
    public void testGetActiveUserById() throws UserIdNotFoundException {
        UserModel user = this.setupUserModelToTest(1l);
        user.setStatus(true);

        Optional<UserModel> userOptional = Optional.of(user);

        when(userRepository.findById(1L)).thenReturn(userOptional);
        userService.getActiveUserById(user.getId());
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

}
