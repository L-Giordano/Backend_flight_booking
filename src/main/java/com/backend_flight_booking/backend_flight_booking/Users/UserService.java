package com.backend_flight_booking.backend_flight_booking.Users;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


public class UserService {

    @Autowired
    private UserRepository userRepository;

    public URI createUser(UserModel userModel){
        userModel.setStatus(Boolean.TRUE);
        userRepository.save(userModel);
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userModel.getId())
                .toUri();
    }
}
