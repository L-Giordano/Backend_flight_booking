package com.backend_flight_booking.backend_flight_booking.Users;

import com.backend_flight_booking.backend_flight_booking.Exceptions.UserIdNotFoundException;
import com.backend_flight_booking.backend_flight_booking.Users.DTO.UserBasicInfoDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public URI createUser(UserModel userModel){
        userModel.setStatus(Boolean.TRUE);
        userRepository.save(userModel);
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userModel.getId())
                .toUri();
    }

    public UserBasicInfoDTO getUserById(Long id) throws UserIdNotFoundException {
        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException("User with ID " + id + " not found"));

        return modelMapper.map(userModel, UserBasicInfoDTO.class);
    }

    @Transactional
    public void updateUser(UserModel userModel) throws UserIdNotFoundException {
        Optional<UserModel> response = userRepository.findById(userModel.getId());
        if(response.isEmpty()){
            throw new UserIdNotFoundException(String.format("User Id %s not Found", userModel.getId()));
        }
        userRepository.save(userModel);
    }


    @Transactional
    public void deleteUser(Long id) throws UserIdNotFoundException {
        Optional<UserModel> responseOptional = userRepository.findById(id);
        if(responseOptional.isEmpty()){
            throw new UserIdNotFoundException(String.format("User Id %s not Found", id));
        }
        UserModel userModel = responseOptional.get();
        //TODO:It might be a good idea to check if the user has active bookings before deactivate it
        userModel.setStatus(Boolean.FALSE);
        userRepository.save(userModel);
    }



}
