package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.dto.LoginRequestDto;
import com.blubank.doctorappointment.dto.LoginResponseDto;
import com.blubank.doctorappointment.dto.RegisterRequestDto;
import com.blubank.doctorappointment.dto.RegisterResponseDto;
import com.blubank.doctorappointment.dto.exception.AccessDeniedException;
import com.blubank.doctorappointment.persistence.entity.AppUser;
import com.blubank.doctorappointment.persistence.entity.Patient;
import com.blubank.doctorappointment.persistence.entity.enumeration.UserRole;
import com.blubank.doctorappointment.persistence.repository.AppUserRepository;
import com.blubank.doctorappointment.persistence.repository.DoctorRepository;
import com.blubank.doctorappointment.security.JWTUtil;
import com.blubank.doctorappointment.service.assembler.AuthServiceAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceUTest {

    @Mock
    private AppUserRepository userRepo;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthServiceAssembler authServiceAssembler;

    @InjectMocks
    private AuthService authService;

    private static final String USERNAME = "shahryarsz";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String TOKEN = "token";

    private RegisterRequestDto registerRequestDto;
    private LoginRequestDto loginRequestDto;
    private AppUser appUser;

    @BeforeEach
    public void setup() {
        registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setUsername(USERNAME);
        registerRequestDto.setPassword(PASSWORD);
        registerRequestDto.setUserRole(UserRole.ROLE_PATIENT);

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername(USERNAME);
        loginRequestDto.setPassword(PASSWORD);

        appUser = new Patient();
        appUser.setUsername(USERNAME);
        appUser.setPassword(ENCODED_PASSWORD);
    }

    @Test
    public void testRegister() {
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(authServiceAssembler.convertToAppUser(registerRequestDto)).thenReturn(appUser);
        RegisterResponseDto response = authService.register(registerRequestDto);
        assertNotNull(response);
        verify(passwordEncoder).encode(PASSWORD);
        verify(authServiceAssembler).convertToAppUser(registerRequestDto);
        verify(userRepo).save(appUser);
    }

    @Test
    public void testLogin_Success() throws AccessDeniedException {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtUtil.generateToken(USERNAME)).thenReturn(TOKEN);
        LoginResponseDto response = authService.login(loginRequestDto);
        assertNotNull(response);
        assertEquals(TOKEN, response.getToken());
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(USERNAME);
    }

    @Test
    public void testLogin_AccessDeniedException() {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new AuthenticationException("invalid credential") {
        });
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            authService.login(loginRequestDto);
        });
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
