package com.blubank.doctorappointment.service.assembler;

import com.blubank.doctorappointment.dto.RegisterRequestDto;
import com.blubank.doctorappointment.persistence.entity.AppUser;
import com.blubank.doctorappointment.persistence.entity.Doctor;
import com.blubank.doctorappointment.persistence.entity.Patient;
import com.blubank.doctorappointment.persistence.entity.enumeration.UserRole;
import org.springframework.stereotype.Component;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024 
 */
@Component
public class AuthServiceAssembler {

    public AppUser convertToAppUser(RegisterRequestDto registerRequestDto) {
        AppUser appUser;
        if (registerRequestDto.getUserRole().equals(UserRole.ROLE_DOCTOR)) {
            appUser = getDoctor(registerRequestDto);
        } else {
            appUser = getPatient(registerRequestDto);
        }
        setCommonAppUserFields(appUser, registerRequestDto);
        return appUser;
    }

    private AppUser getPatient(RegisterRequestDto registerRequestDto) {
        return new Patient();
    }

    private AppUser getDoctor(RegisterRequestDto registerRequestDto) {
        return new Doctor();
    }

    private void setCommonAppUserFields(AppUser appUser, RegisterRequestDto registerRequestDto) {
        appUser.setPhoneNumber(registerRequestDto.getPhoneNumber());
        appUser.setUsername(registerRequestDto.getUsername());
        appUser.setName(registerRequestDto.getName());
        appUser.setPassword(registerRequestDto.getPassword());
        appUser.setRoleName(registerRequestDto.getUserRole());
    }
}
