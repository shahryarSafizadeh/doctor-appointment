package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesRequestDto;
import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesResponseDto;
import com.blubank.doctorappointment.dto.ViewAllAppointmentsResponseDto;
import com.blubank.doctorappointment.dto.exception.*;
import com.blubank.doctorappointment.persistence.entity.AppUser;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Doctor;
import com.blubank.doctorappointment.persistence.repository.AppUserRepository;
import com.blubank.doctorappointment.persistence.repository.AppointmentRepository;
import com.blubank.doctorappointment.persistence.repository.DoctorRepository;
import com.blubank.doctorappointment.service.assembler.DoctorServiceAssembler;
import com.blubank.doctorappointment.util.LockManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024
 */
@ExtendWith(MockitoExtension.class)
public class DoctorServiceUTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private DoctorServiceAssembler doctorServiceAssembler;

    @Mock
    private LockManager lockManager;

    @InjectMocks
    private DoctorService doctorService;

    private static final Long DOCTOR_ID = 1L;
    private static final Long APPOINTMENT_ID = 1L;
    private static final String USERNAME = "DR.yegane";
    private static final LocalDateTime START_TIME = LocalDateTime.now();
    private static final LocalDateTime END_TIME = START_TIME.plusHours(1);

    private Doctor doctor;
    private Appointment appointment;
    private AppUser user;

    @BeforeEach
    public void setup() {
        doctor = new Doctor();
        doctor.setId(DOCTOR_ID);

        appointment = new Appointment();
        appointment.setId(APPOINTMENT_ID);
        appointment.setDoctor(doctor);
        appointment.setTaken(false);
        appointment.setDate(START_TIME);
        appointment.setStartTime(START_TIME);
        appointment.setEndTime(END_TIME);

        user = new AppUser();
        user.setUsername(USERNAME);
        user.setId(DOCTOR_ID);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(USERNAME);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testSetOpenAppointmentTimes_throwsUserNotFoundException() {
        SetOpenAppointmentTimesRequestDto request = new SetOpenAppointmentTimesRequestDto();
        request.setStartTime(START_TIME);
        request.setEndTime(END_TIME);

        when(appUserRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            doctorService.setOpenAppointmentTimes(request);
        });
    }

    @Test
    public void testSetOpenAppointmentTimes_Success() throws ValidationException, UserNotFoundException {
        SetOpenAppointmentTimesRequestDto request = new SetOpenAppointmentTimesRequestDto();
        request.setStartTime(START_TIME);
        request.setEndTime(END_TIME);

        when(appUserRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(doctorRepository.findById(DOCTOR_ID)).thenReturn(Optional.of(doctor));
        when(doctorServiceAssembler.convertNewAppointment(any(LocalDateTime.class), eq(doctor))).thenReturn(appointment);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        SetOpenAppointmentTimesResponseDto setOpenAppointmentTimesResponseDto = new SetOpenAppointmentTimesResponseDto();
        setOpenAppointmentTimesResponseDto.setAppointmentsCount(2);
        when(doctorServiceAssembler.convertSetOpenAppointmentTimesResponseDto(anyInt())).thenReturn(setOpenAppointmentTimesResponseDto);

        SetOpenAppointmentTimesResponseDto response = doctorService.setOpenAppointmentTimes(request);
        assertNotNull(response);
        assertEquals(2, response.getAppointmentsCount());
    }

    @Test
    public void testViewAllAppointments_Success() throws UserNotFoundException {
        List<Appointment> appointments = Arrays.asList(appointment);
        ViewAllAppointmentsResponseDto expectedResponse = new ViewAllAppointmentsResponseDto();

        when(appUserRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(appointmentRepository.findByDoctorId(DOCTOR_ID)).thenReturn(appointments);
        when(doctorServiceAssembler.convertViewAllAppointmentsResponseDto(appointments)).thenReturn(expectedResponse);

        ViewAllAppointmentsResponseDto response = doctorService.viewAllAppointments();
        assertNotNull(response);
    }

    @Test
    @Transactional
    public void testDeleteAppointment_throwsNoAppointmentFoundException() throws UserNotFoundException {
        when(appUserRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(appointmentRepository.findByDoctorIdAndId(DOCTOR_ID, APPOINTMENT_ID)).thenReturn(Optional.empty());
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(APPOINTMENT_ID)).thenReturn(lock);

        NoAppointmentFoundException exception = assertThrows(NoAppointmentFoundException.class, () -> {
            doctorService.deleteAppointment(APPOINTMENT_ID);
        });

        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testDeleteAppointment_throwsAppointmentIsTakenException() throws UserNotFoundException {
        appointment.setTaken(true);
        when(appUserRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(appointmentRepository.findByDoctorIdAndId(DOCTOR_ID, APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(APPOINTMENT_ID)).thenReturn(lock);

        AppointmentIsTakenException exception = assertThrows(AppointmentIsTakenException.class, () -> {
            doctorService.deleteAppointment(APPOINTMENT_ID);
        });

        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testDeleteAppointment_Success() throws NoAppointmentFoundException, AppointmentIsTakenException, AccessDeniedException, UserNotFoundException {
        when(appUserRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(appointmentRepository.findByDoctorIdAndId(DOCTOR_ID, APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(APPOINTMENT_ID)).thenReturn(lock);

        doctorService.deleteAppointment(APPOINTMENT_ID);

        verify(appointmentRepository).deleteById(APPOINTMENT_ID);
        verify(lock).lock();
        verify(lock).unlock();
    }
}
