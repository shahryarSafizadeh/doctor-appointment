package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.dto.ReserveAppointmentRequestDto;
import com.blubank.doctorappointment.dto.ReserveAppointmentResponseDto;
import com.blubank.doctorappointment.dto.ViewAllAppointmentsResponseDto;
import com.blubank.doctorappointment.dto.exception.AppointmentIsTakenException;
import com.blubank.doctorappointment.dto.exception.NoAppointmentFoundException;
import com.blubank.doctorappointment.dto.exception.UserNotFoundException;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Patient;
import com.blubank.doctorappointment.persistence.repository.AppointmentRepository;
import com.blubank.doctorappointment.persistence.repository.PatientRepository;
import com.blubank.doctorappointment.service.assembler.GeneralServiceAssembler;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private PatientRepository patientRepository;

    @Mock
    private GeneralServiceAssembler generalServiceAssembler;

    @Mock
    private LockManager lockManager;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private Appointment appointment;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    public void setup() {
        patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");
        patient.setPhoneNumber("123456789");

        startTime = LocalDateTime.now();
        endTime = startTime.plusMinutes(30);

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setTaken(false);
        appointment.setDate(LocalDateTime.now());
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
    }

    @Test
    public void testViewAllOpenAppointments() {
        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentRepository.findByIsTakenFalseAndStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(appointments);
        when(generalServiceAssembler.convertViewAllAppointmentsResponseDto(anyList())).thenReturn(new ViewAllAppointmentsResponseDto());
        ViewAllAppointmentsResponseDto response = patientService.viewAllOpenAppointments(startTime.toLocalDate().toString());
        assertNotNull(response);
    }

    @Test
    @Transactional
    public void testReserveAppointment_NoAppointmentFoundException() {
        ReserveAppointmentRequestDto requestDto = new ReserveAppointmentRequestDto();
        requestDto.setAppointmentId(1L);
        requestDto.setPatientId(1L);
        when(appointmentRepository.findByIdAndIsTakenFalse(anyLong())).thenReturn(Optional.empty());
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(anyLong())).thenReturn(lock);
        assertThrows(NoAppointmentFoundException.class, () -> {
            patientService.reserveAppointment(requestDto);
        });
        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testReserveAppointment_AppointmentIsTakenException() {
        ReserveAppointmentRequestDto requestDto = new ReserveAppointmentRequestDto();
        requestDto.setAppointmentId(1L);
        requestDto.setPatientId(1L);
        appointment.setTaken(true);
        when(appointmentRepository.findByIdAndIsTakenFalse(anyLong())).thenReturn(Optional.of(appointment));
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(anyLong())).thenReturn(lock);
        assertThrows(AppointmentIsTakenException.class, () -> {
            patientService.reserveAppointment(requestDto);
        });
        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testReserveAppointment_UserNotFoundException() {
        ReserveAppointmentRequestDto requestDto = new ReserveAppointmentRequestDto();
        requestDto.setAppointmentId(1L);
        requestDto.setPatientId(1L);
        when(appointmentRepository.findByIdAndIsTakenFalse(anyLong())).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(anyLong())).thenReturn(lock);
        assertThrows(UserNotFoundException.class, () -> {
            patientService.reserveAppointment(requestDto);
        });
        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testReserveAppointment_Success() throws NoAppointmentFoundException, AppointmentIsTakenException, UserNotFoundException {
        ReserveAppointmentRequestDto requestDto = new ReserveAppointmentRequestDto();
        requestDto.setAppointmentId(1L);
        requestDto.setPatientId(1L);
        when(appointmentRepository.findByIdAndIsTakenFalse(anyLong())).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));
        when(generalServiceAssembler.convertReserveAppointmentResponseDto(any(Appointment.class))).thenReturn(new ReserveAppointmentResponseDto());
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(anyLong())).thenReturn(lock);
        ReserveAppointmentResponseDto response = patientService.reserveAppointment(requestDto);
        assertNotNull(response);
        verify(appointmentRepository).save(appointment);
        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    public void testViewPersonalAppointments() {
        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentRepository.findByPatientId(anyLong())).thenReturn(appointments);
        when(generalServiceAssembler.convertViewAllAppointmentsResponseDto(anyList())).thenReturn(new ViewAllAppointmentsResponseDto());
        ViewAllAppointmentsResponseDto response = patientService.viewPersonalAppointments(1L);
        assertNotNull(response);
    }
}
