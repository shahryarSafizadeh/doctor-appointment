package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.dto.LoginRequestDto;
import com.blubank.doctorappointment.dto.LoginResponseDto;
import com.blubank.doctorappointment.dto.RegisterRequestDto;
import com.blubank.doctorappointment.dto.RegisterResponseDto;
import com.blubank.doctorappointment.dto.exception.AccessDeniedException;
import com.blubank.doctorappointment.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authservice;

    /**
     * Registers a new user.
     *
     * @param registerRequestDto the {@link RegisterRequestDto} containing the registration details
     * @return a {@link RegisterResponseDto} indicating the result of the registration process
     */
    @PostMapping(value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RegisterResponseDto register(@RequestBody RegisterRequestDto registerRequestDto) {
        return authservice.register(registerRequestDto);
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param loginRequestDto the {@link LoginRequestDto} containing the login credentials
     * @return a {@link LoginResponseDto} containing the JWT token
     * @throws AccessDeniedException if the authentication fails due to invalid credentials
     */
    @PostMapping(value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) throws AccessDeniedException {
        return authservice.login(loginRequestDto);
    }
}