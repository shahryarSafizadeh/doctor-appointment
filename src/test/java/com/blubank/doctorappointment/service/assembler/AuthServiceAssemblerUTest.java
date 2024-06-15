package com.blubank.doctorappointment.service.assembler;


import com.blubank.doctorappointment.dto.RegisterRequestDto;
import com.blubank.doctorappointment.persistence.entity.AppUser;
import com.blubank.doctorappointment.persistence.entity.Doctor;
import com.blubank.doctorappointment.persistence.entity.Patient;
import com.blubank.doctorappointment.persistence.entity.enumeration.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024 
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceAssemblerUTest {

    @InjectMocks
    private AuthServiceAssembler authServiceAssembler;

    private RegisterRequestDto registerRequestDto;

    private final String NAME = "Shahryar";
    private final String MOBILE = "123456789";
    private final String USERNAME = "aghaShahryar";
    private final String PASSWORD = "password";

    @BeforeEach
    public void setup() {
        registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setPhoneNumber(MOBILE);
        registerRequestDto.setUsername(USERNAME);
        registerRequestDto.setName(NAME);
        registerRequestDto.setPassword(PASSWORD);
    }

    @Test
    public void testConvertToAppUser_Doctor() {
        registerRequestDto.setUserRole(UserRole.ROLE_DOCTOR);

        AppUser appUser = authServiceAssembler.convertToAppUser(registerRequestDto);

        assertNotNull(appUser);
        assertTrue(appUser instanceof Doctor);
        assertEquals(MOBILE, appUser.getPhoneNumber());
        assertEquals(USERNAME, appUser.getUsername());
        assertEquals(NAME, appUser.getName());
        assertEquals(PASSWORD, appUser.getPassword());
        assertEquals(UserRole.ROLE_DOCTOR, appUser.getRoleName());
    }

    @Test
    public void testConvertToAppUser_Patient() {
        registerRequestDto.setUserRole(UserRole.ROLE_PATIENT);

        AppUser appUser = authServiceAssembler.convertToAppUser(registerRequestDto);

        assertNotNull(appUser);
        assertTrue(appUser instanceof Patient);
        assertEquals(MOBILE, appUser.getPhoneNumber());
        assertEquals(USERNAME, appUser.getUsername());
        assertEquals(NAME, appUser.getName());
        assertEquals(PASSWORD, appUser.getPassword());
        assertEquals(UserRole.ROLE_PATIENT, appUser.getRoleName());
    }
}
