package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.dto.ReserveAppointmentRequestDto;
import com.blubank.doctorappointment.dto.ReserveAppointmentResponseDto;
import com.blubank.doctorappointment.dto.ViewAllOpenAppointmentResponseDto;
import com.blubank.doctorappointment.dto.ViewPersonalAppointmentsResponseDto;
import com.blubank.doctorappointment.dto.exception.UserNotFoundException;
import com.blubank.doctorappointment.persistence.entity.AppUser;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Patient;
import com.blubank.doctorappointment.persistence.repository.AppUserRepository;
import com.blubank.doctorappointment.persistence.repository.AppointmentRepository;
import com.blubank.doctorappointment.persistence.repository.PatientRepository;
import com.blubank.doctorappointment.service.assembler.GeneralServiceAssembler;
import com.blubank.doctorappointment.service.assembler.PatientServiceAssembler;
import com.blubank.doctorappointment.util.LockManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024
 */
@ExtendWith(MockitoExtension.class)
public class PatientServiceUTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private GeneralServiceAssembler generalServiceAssembler;

    @Mock
    private PatientServiceAssembler patientServiceAssembler;

    @Mock
    private LockManager lockManager;

    @InjectMocks
    private PatientService patientService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Test
    void testViewAllOpenAppointments() {
        String date = "2023-06-15";
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59);

        List<Appointment> appointmentList = Collections.emptyList();
        when(appointmentRepository.findByIsTakenFalseAndStartTimeBetween(startOfDay, endOfDay)).thenReturn(appointmentList);

        ViewAllOpenAppointmentResponseDto responseDto = new ViewAllOpenAppointmentResponseDto();
        when(patientServiceAssembler.convertViewAllOpenAppointmentsResponseDto(appointmentList)).thenReturn(responseDto);

        ViewAllOpenAppointmentResponseDto result = patientService.viewAllOpenAppointments(date);

        verify(appointmentRepository).findByIsTakenFalseAndStartTimeBetween(startOfDay, endOfDay);
        verify(patientServiceAssembler).convertViewAllOpenAppointmentsResponseDto(appointmentList);

        assertEquals(responseDto, result);
    }

    @Test
    void testReserveAppointment() throws Exception {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        ReserveAppointmentRequestDto request = new ReserveAppointmentRequestDto();
        request.setAppointmentId(1L);

        Long patientId = 1L;
        when(authentication.getPrincipal()).thenReturn("shahryar");
        AppUser appUser = new AppUser();
        appUser.setId(patientId);
        when(appUserRepository.findByUsername("shahryar")).thenReturn(Optional.of(appUser));

        Lock lock = mock(Lock.class);
        when(lockManager.getLock(request.getAppointmentId())).thenReturn(lock);

        Appointment appointment = new Appointment();
        appointment.setId(request.getAppointmentId());
        appointment.setTaken(false);
        when(appointmentRepository.findByIdAndIsTakenFalse(request.getAppointmentId())).thenReturn(Optional.of(appointment));

        Patient patient = new Patient();
        patient.setId(patientId);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        ReserveAppointmentResponseDto responseDto = new ReserveAppointmentResponseDto();
        when(patientServiceAssembler.convertReserveAppointmentResponseDto(appointment)).thenReturn(responseDto);

        ReserveAppointmentResponseDto result = patientService.reserveAppointment(request);

        verify(lockManager).getLock(request.getAppointmentId());
        verify(lock).lock();
        verify(appointmentRepository).findByIdAndIsTakenFalse(request.getAppointmentId());
        verify(patientRepository).findById(patientId);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(lock).unlock();
        verify(lockManager).removeLock(request.getAppointmentId());
        verify(patientServiceAssembler).convertReserveAppointmentResponseDto(appointment);

        assertEquals(responseDto, result);
    }

    @Test
    void testViewPersonalAppointments() throws UserNotFoundException {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        Long patientId = 1L;
        when(authentication.getPrincipal()).thenReturn("shahryar");
        AppUser appUser = new AppUser();
        appUser.setId(patientId);
        when(appUserRepository.findByUsername("shahryar")).thenReturn(Optional.of(appUser));

        List<Appointment> appointmentList = Collections.emptyList();
        when(appointmentRepository.findByPatientId(patientId)).thenReturn(appointmentList);

        ViewPersonalAppointmentsResponseDto responseDto = new ViewPersonalAppointmentsResponseDto();
        when(patientServiceAssembler.convertViewPersonalAppointmentsResponseDto(appointmentList)).thenReturn(responseDto);

        ViewPersonalAppointmentsResponseDto result = patientService.viewPersonalAppointments();

        verify(appointmentRepository).findByPatientId(patientId);
        verify(patientServiceAssembler).convertViewPersonalAppointmentsResponseDto(appointmentList);

        assertEquals(responseDto, result);
    }
}
