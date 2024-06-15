package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesRequestDto;
import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesResponseDto;
import com.blubank.doctorappointment.dto.ViewAllAppointmentsResponseDto;
import com.blubank.doctorappointment.dto.exception.*;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Doctor;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceUTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorServiceAssembler doctorServiceAssembler;

    @Mock
    private LockManager lockManager;

    @InjectMocks
    private DoctorService doctorService;

    private static final Long DOCTOR_ID = 1L;
    private static final Long APPOINTMENT_ID = 1L;
    private static final LocalDateTime START_TIME = LocalDateTime.now();
    private static final LocalDateTime END_TIME = START_TIME.plusHours(1);
    private static final int APPOINTMENTS_COUNT = 2;

    private Doctor doctor;
    private Appointment appointment;

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
    }

    @Test
    public void testSetOpenAppointmentTimes_ValidationException() {
        SetOpenAppointmentTimesRequestDto request = new SetOpenAppointmentTimesRequestDto();
        request.setDoctorId(DOCTOR_ID);
        request.setStartTime(END_TIME);
        request.setEndTime(START_TIME);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            doctorService.setOpenAppointmentTimes(request);
        });
        assertEquals("Invalid time range.", exception.getMessage());
    }

    @Test
    public void testSetOpenAppointmentTimes_UserNotFoundException() {
        SetOpenAppointmentTimesRequestDto request = new SetOpenAppointmentTimesRequestDto();
        request.setDoctorId(DOCTOR_ID);
        request.setStartTime(START_TIME);
        request.setEndTime(END_TIME);
        when(doctorRepository.findById(DOCTOR_ID)).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            doctorService.setOpenAppointmentTimes(request);
        });
        assertEquals("Doctor not found", exception.getMessage());
    }

    @Test
    public void testSetOpenAppointmentTimes_Success() throws ValidationException, UserNotFoundException {
        SetOpenAppointmentTimesRequestDto request = new SetOpenAppointmentTimesRequestDto();
        request.setDoctorId(DOCTOR_ID);
        request.setStartTime(START_TIME);
        request.setEndTime(END_TIME);
        when(doctorRepository.findById(DOCTOR_ID)).thenReturn(Optional.of(doctor));
        when(doctorServiceAssembler.convertNewAppointment(any(LocalDateTime.class), eq(doctor))).thenReturn(appointment);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        SetOpenAppointmentTimesResponseDto setOpenAppointmentTimesResponseDto = new SetOpenAppointmentTimesResponseDto();
        setOpenAppointmentTimesResponseDto.setAppointmentsCount(APPOINTMENTS_COUNT);
        when(doctorServiceAssembler.convertSetOpenAppointmentTimesResponseDto(anyInt())).thenReturn(setOpenAppointmentTimesResponseDto);
        SetOpenAppointmentTimesResponseDto response = doctorService.setOpenAppointmentTimes(request);
        assertNotNull(response);
        assertEquals(APPOINTMENTS_COUNT, response.getAppointmentsCount());
    }

    @Test
    public void testViewAllAppointments_Success() throws UserNotFoundException {
        List<Appointment> appointments = Arrays.asList(appointment);
        ViewAllAppointmentsResponseDto expectedResponse = new ViewAllAppointmentsResponseDto();
        when(appointmentRepository.findByDoctorId(DOCTOR_ID)).thenReturn(appointments);
        when(doctorServiceAssembler.convertViewAllAppointmentsResponseDto(appointments)).thenReturn(expectedResponse);
        ViewAllAppointmentsResponseDto response = doctorService.viewAllAppointments(DOCTOR_ID);
        assertNotNull(response);
    }

    @Test
    @Transactional
    public void testDeleteAppointment_NoAppointmentFoundException() {
        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.empty());
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(APPOINTMENT_ID)).thenReturn(lock);
        NoAppointmentFoundException exception = assertThrows(NoAppointmentFoundException.class, () -> {
            doctorService.deleteAppointment(APPOINTMENT_ID, DOCTOR_ID);
        });
        assertEquals("Appointment not found", exception.getMessage());
        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testDeleteAppointment_AppointmentIsTakenException() {
        appointment.setTaken(true);
        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(APPOINTMENT_ID)).thenReturn(lock);
        AppointmentIsTakenException exception = assertThrows(AppointmentIsTakenException.class, () -> {
            doctorService.deleteAppointment(APPOINTMENT_ID, DOCTOR_ID);
        });
        assertEquals("Cannot delete a taken appointment", exception.getMessage());
        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testDeleteAppointment_AccessDeniedException() {
        Doctor anotherDoctor = new Doctor();
        anotherDoctor.setId(2L);
        appointment.setDoctor(anotherDoctor);
        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(APPOINTMENT_ID)).thenReturn(lock);
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            doctorService.deleteAppointment(APPOINTMENT_ID, DOCTOR_ID);
        });
        assertEquals("Doctor not authorized to delete this appointment", exception.getMessage());
        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testDeleteAppointment_Success() throws NoAppointmentFoundException, AppointmentIsTakenException, AccessDeniedException {
        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(APPOINTMENT_ID)).thenReturn(lock);
        doctorService.deleteAppointment(APPOINTMENT_ID, DOCTOR_ID);
        verify(appointmentRepository).delete(appointment);
        verify(lock).lock();
        verify(lock).unlock();
    }
}

