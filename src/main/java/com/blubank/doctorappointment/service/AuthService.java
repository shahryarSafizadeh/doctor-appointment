package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.dto.LoginRequestDto;
import com.blubank.doctorappointment.dto.LoginResponseDto;
import com.blubank.doctorappointment.dto.RegisterRequestDto;
import com.blubank.doctorappointment.dto.RegisterResponseDto;
import com.blubank.doctorappointment.dto.exception.AccessDeniedException;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorType;
import com.blubank.doctorappointment.persistence.entity.AppUser;
import com.blubank.doctorappointment.persistence.repository.AppUserRepository;
import com.blubank.doctorappointment.persistence.repository.DoctorRepository;
import com.blubank.doctorappointment.security.JWTUtil;
import com.blubank.doctorappointment.service.assembler.AuthServiceAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AppUserRepository userRepo;
    private final DoctorRepository doctorRepository;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthServiceAssembler authServiceAssembler;

    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
        log.info("Registering new user with username: {}", registerRequestDto.getUsername());
        String encodedPass = passwordEncoder.encode(registerRequestDto.getPassword());
        registerRequestDto.setPassword(encodedPass);

        AppUser appUser = authServiceAssembler.convertToAppUser(registerRequestDto);
        userRepo.save(appUser);

        log.info("User with username: {} registered successfully", registerRequestDto.getUsername());
        return new RegisterResponseDto();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws AccessDeniedException {
        log.info("Logging in user with username: {}", loginRequestDto.getUsername());
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());

            authManager.authenticate(authInputToken);

            String token = jwtUtil.generateToken(loginRequestDto.getUsername());

            LoginResponseDto responseDto = new LoginResponseDto();
            responseDto.setToken(token);

            log.info("User with username: {} logged in successfully", loginRequestDto.getUsername());
            return responseDto;
        } catch (AuthenticationException authExc) {
            log.error("Invalid credentials for username: {}", loginRequestDto.getUsername());
            throw new AccessDeniedException("Invalid credential.", ErrorType.AUTHENTICATION);
        }
    }
}
