package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.dto.*;
import com.blubank.doctorappointment.dto.exception.AppointmentIsTakenException;
import com.blubank.doctorappointment.dto.exception.NoAppointmentFoundException;
import com.blubank.doctorappointment.dto.exception.UserNotFoundException;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Patient;
import com.blubank.doctorappointment.persistence.repository.AppointmentRepository;
import com.blubank.doctorappointment.persistence.repository.PatientRepository;
import com.blubank.doctorappointment.service.assembler.GeneralServiceAssembler;
import com.blubank.doctorappointment.service.assembler.PatientServiceAssembler;
import com.blubank.doctorappointment.util.LockManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;
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
    private PatientServiceAssembler patientServiceAssembler;

    @Mock
    private LockManager lockManager;

    @InjectMocks
    private PatientService patientService;

    private static final Long PATIENT_ID = 1L;
    private static final Long APPOINTMENT_ID = 1L;
    private static final String DATE_STRING = "2023-06-01";
    private static final LocalDate DATE = LocalDate.parse(DATE_STRING);
    private static final LocalDateTime START_TIME = DATE.atStartOfDay();
    private static final LocalDateTime END_TIME = DATE.atTime(23, 59, 59);

    private Patient patient;
    private Appointment appointment;

    @BeforeEach
    public void setup() {
        patient = new Patient();
        patient.setId(PATIENT_ID);
        patient.setName("John Doe");
        patient.setPhoneNumber("123456789");

        appointment = new Appointment();
        appointment.setId(APPOINTMENT_ID);
        appointment.setPatient(patient);
        appointment.setTaken(false);
        appointment.setDate(DATE.atStartOfDay());
        appointment.setStartTime(START_TIME);
        appointment.setEndTime(END_TIME);
    }

    @Test
    public void testViewAllOpenAppointments() {
        List<Appointment> appointments = Arrays.asList(appointment);
        ViewAllOpenAppointmentResponseDto expectedResponse = new ViewAllOpenAppointmentResponseDto();

        when(appointmentRepository.findByIsTakenFalseAndStartTimeBetween(START_TIME, END_TIME)).thenReturn(appointments);
        when(patientServiceAssembler.convertViewAllOpenAppointmentsResponseDto(appointments)).thenReturn(expectedResponse);

        ViewAllOpenAppointmentResponseDto response = patientService.viewAllOpenAppointments(DATE_STRING);
        assertNotNull(response);
        verify(appointmentRepository).findByIsTakenFalseAndStartTimeBetween(START_TIME, END_TIME);
        verify(patientServiceAssembler).convertViewAllOpenAppointmentsResponseDto(appointments);
    }

    @Test
    @Transactional
    public void testReserveAppointment_NoAppointmentFoundException() {
        ReserveAppointmentRequestDto request = new ReserveAppointmentRequestDto();
        request.setAppointmentId(APPOINTMENT_ID);
        request.setPatientId(PATIENT_ID);

        when(appointmentRepository.findByIdAndIsTakenFalse(APPOINTMENT_ID)).thenReturn(Optional.empty());
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(APPOINTMENT_ID)).thenReturn(lock);

        NoAppointmentFoundException exception = assertThrows(NoAppointmentFoundException.class, () -> {
            patientService.reserveAppointment(request);
        });
        assertEquals("Appointment not found", exception.getMessage());

        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testReserveAppointment_AppointmentIsTakenException() {
        appointment.setTaken(true);
        ReserveAppointmentRequestDto request = new ReserveAppointmentRequestDto();
        request.setAppointmentId(APPOINTMENT_ID);
        request.setPatientId(PATIENT_ID);

        when(appointmentRepository.findByIdAndIsTakenFalse(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(APPOINTMENT_ID)).thenReturn(lock);

        AppointmentIsTakenException exception = assertThrows(AppointmentIsTakenException.class, () -> {
            patientService.reserveAppointment(request);
        });
        assertEquals("Cannot delete a taken appointment", exception.getMessage());

        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testReserveAppointment_UserNotFoundException() {
        ReserveAppointmentRequestDto request = new ReserveAppointmentRequestDto();
        request.setAppointmentId(APPOINTMENT_ID);
        request.setPatientId(PATIENT_ID);

        when(appointmentRepository.findByIdAndIsTakenFalse(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.empty());
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(APPOINTMENT_ID)).thenReturn(lock);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            patientService.reserveAppointment(request);
        });
        assertEquals("Patient not found", exception.getMessage());

        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testReserveAppointment_Success() throws NoAppointmentFoundException, AppointmentIsTakenException, UserNotFoundException {
        ReserveAppointmentRequestDto request = new ReserveAppointmentRequestDto();
        request.setAppointmentId(APPOINTMENT_ID);
        request.setPatientId(PATIENT_ID);

        when(appointmentRepository.findByIdAndIsTakenFalse(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        when(patientServiceAssembler.convertReserveAppointmentResponseDto(any(Appointment.class))).thenReturn(new ReserveAppointmentResponseDto());
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(APPOINTMENT_ID)).thenReturn(lock);

        ReserveAppointmentResponseDto response = patientService.reserveAppointment(request);
        assertNotNull(response);

        verify(appointmentRepository).save(appointment);
        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    public void testViewPersonalAppointments() {
        List<Appointment> appointments = Arrays.asList(appointment);
        ViewPersonalAppointmentsResponseDto expectedResponse = new ViewPersonalAppointmentsResponseDto();

        when(appointmentRepository.findByPatientId(PATIENT_ID)).thenReturn(appointments);
        when(patientServiceAssembler.convertViewPersonalAppointmentsResponseDto(appointments)).thenReturn(expectedResponse);

        ViewPersonalAppointmentsResponseDto response = patientService.viewPersonalAppointments(PATIENT_ID);
        assertNotNull(response);
        verify(appointmentRepository).findByPatientId(PATIENT_ID);
        verify(patientServiceAssembler).convertViewPersonalAppointmentsResponseDto(appointments);
    }
}