import com.backend_flight_booking.backend_flight_booking.Exceptions.UserIdNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Users.DTO.UserBasicInfoDTO;
import com.backend_flight_booking.backend_flight_booking.Users.UserController;
import com.backend_flight_booking.backend_flight_booking.Users.UserModel;
import com.backend_flight_booking.backend_flight_booking.Users.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserControllerTests.class)
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTests {

    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;


    public UserModel setupUserModelForBody(Long id){
        UserModel user = new UserModel();
        user.setFirstName("User");
        user.setLastName("Test");
        user.setUserName("userTest");
        user.setEmail("user@test.com");
        user.setPassword("1234");
        user.setId(id);

        return user;
    }

    public UserBasicInfoDTO setupUserBasicInfoDTO(){
        UserBasicInfoDTO userBasicInfoDTO = new UserBasicInfoDTO();
        userBasicInfoDTO.setFirstName("User");
        userBasicInfoDTO.setLastName("Test");
        userBasicInfoDTO.setUserName("userTest");
        userBasicInfoDTO.setEmail("user@test.com");
        userBasicInfoDTO.setId(1L);
        userBasicInfoDTO.setStatus(true);

        return userBasicInfoDTO;
    }



    @Test
    public void testWhenCreateUserSuccess(){

        UserModel user = this.setupUserModelForBody(1l);
        URI uri = URI.create(String.format("localHost:8080/users/%s", user.getId()));

        when(userService.createUser(user)).thenReturn(uri);

        ResponseEntity<Object> expected = ResponseEntity.created(uri).build();
        ResponseEntity<Object> result = userController.createUser(user);

        assertEquals(expected, result);
    }

    @Test
    public void testWhenCreateUserGetExceptionReturnsInternalServerError(){

        UserModel user = this.setupUserModelForBody(1L);

        when(userService.createUser(user)).thenThrow(new IllegalArgumentException());

        ResponseEntity<Object> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        ResponseEntity<Object> result = userController.createUser(user);

        assertEquals(expected, result);
    }

    @Test
    public void testWhenGetUserByIdSuccess() throws UserIdNotFoundException {
        Long id = 1L;
        UserBasicInfoDTO userBasicInfoDTO = this.setupUserBasicInfoDTO();

        when(userService.getActiveUserById(id)).thenReturn(userBasicInfoDTO);

        ResponseEntity<Object> expected = ResponseEntity.ok(userBasicInfoDTO);
        ResponseEntity<Object> result = userController.getUser(id);

        assertEquals(expected, result);
    }

    @Test
    public void testWhenGetUserByIdNoFound() throws UserIdNotFoundException {
        Long id = 1L;
        UserBasicInfoDTO userBasicInfoDTO = this.setupUserBasicInfoDTO();

        when(userService.getActiveUserById(id)).thenThrow(UserIdNotFoundException.class);

        ResponseEntity<Object> expected = ResponseEntity.badRequest().build();
        ResponseEntity<Object> result = userController.getUser(id);

        assertEquals(expected, result);

    }

    @Test
    public void testWhenGetUserByIdHasInternalServerError() throws UserIdNotFoundException {
        Long id = 1L;

        when(userService.getActiveUserById(id)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<Object> expected = ResponseEntity.internalServerError().build();
        ResponseEntity<Object> result = userController.getUser(id);

        assertEquals(expected, result);

    }

    @Test
    public void testWhenUpdateUserByIdSuccess() throws UserIdNotFoundException {
        UserModel user = this.setupUserModelForBody(1L);

        doNothing().when(userService).updateUser(user);

        ResponseEntity<Object> expected = ResponseEntity.noContent().build();
        ResponseEntity<Object> result = userController.updateUser(user);

        assertEquals(expected, result);
    }

    @Test
    public void testWhenUpdateUserByIdUserNotFound() throws UserIdNotFoundException {
        UserModel user = this.setupUserModelForBody(1L);

        doThrow(
                new UserIdNotFoundException(
                        String.format("User Id %s not Found", user.getId().toString())))
                .when(userService).updateUser(user);

        ResponseEntity<Object> expected = ResponseEntity
                .badRequest()
                .body(String.format("User Id %s not Found", user.getId().toString()));
        ResponseEntity<Object> result = userController.updateUser(user);

        assertEquals(expected, result);
    }

    @Test
    public void testWhenUpdateUserByIdHasInternalServerError() throws UserIdNotFoundException {
        UserModel user = this.setupUserModelForBody(1L);

        doThrow(
                new IllegalArgumentException())
                .when(userService).updateUser(user);

        ResponseEntity<Object> expected = ResponseEntity.internalServerError().build();
        ResponseEntity<Object> result = userController.updateUser(user);

        assertEquals(expected, result);
    }

    @Test
    public void testWhenDeleteUserByIdSuccess() throws UserIdNotFoundException {
        Long id = 1L;

        doNothing().when(userService).deleteUser(id);

        ResponseEntity<Object> expected = ResponseEntity.noContent().build();
        ResponseEntity<Object> result = userController.deleteUser(id);

        assertEquals(expected, result);
    }

    @Test
    public void testWhenDeleteUserByIdUserNotFound() throws UserIdNotFoundException {
        Long id = 1L;

        doThrow(
                new UserIdNotFoundException(
                        String.format("User Id %s not Found", id.toString())))
                .when(userService).deleteUser(id);

        ResponseEntity<Object> expected = ResponseEntity
                .badRequest()
                .body(String.format("User Id %s not Found", id.toString()));
        ResponseEntity<Object> result = userController.deleteUser(id);

        assertEquals(expected, result);
    }

    @Test
    public void testWhenDeleteUserByIdHasInternalServerError() throws UserIdNotFoundException {
        Long id = 1L;

        doThrow(
                new IllegalArgumentException())
                .when(userService).deleteUser(id);

        ResponseEntity<Object> expected = ResponseEntity.internalServerError().build();
        ResponseEntity<Object> result = userController.deleteUser(id);

        assertEquals(expected, result);
    }



}
