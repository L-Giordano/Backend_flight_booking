import com.backend_flight_booking.backend_flight_booking.Users.UserModel;
import com.backend_flight_booking.backend_flight_booking.Users.UserRepository;
import com.backend_flight_booking.backend_flight_booking.Users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
