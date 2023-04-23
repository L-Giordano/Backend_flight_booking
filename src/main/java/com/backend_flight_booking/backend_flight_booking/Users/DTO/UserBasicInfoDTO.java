package com.backend_flight_booking.backend_flight_booking.Users.DTO;

import lombok.Data;

@Data
public class UserBasicInfoDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private Boolean status;
}
