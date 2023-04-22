package com.backend_flight_booking.backend_flight_booking.Users;


import com.backend_flight_booking.backend_flight_booking.Reservations.ReservationModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter@EqualsAndHashCode
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "userName field can`t be empty")
    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, unique = true)
    @Email(message = "Email must have the correct format")
    @NotBlank(message = "email field can`t be empty")
    private String email;

    @NotBlank(message = "password field can`t be empty")
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationModel> reservation = new ArrayList<>();

    @Column
    private Boolean status;
}