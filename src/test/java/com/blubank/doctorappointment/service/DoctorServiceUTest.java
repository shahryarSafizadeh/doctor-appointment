package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesRequestDto;
import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesResponseDto;
import com.blubank.doctorappointment.dto.ViewAllAppointmentsResponseDto;
import com.blubank.doctorappointment.dto.exception.*;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Doctor;
import com.blubank.doctorappointment.persistence.repository.AppointmentRepository;
import com.blubank.doctorappointment.persistence.repository.DoctorRepository;
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
 * @since 6/14/2024
 */
@ExtendWith(MockitoExtension.class)
public class DoctorServiceUTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private GeneralServiceAssembler generalServiceAssembler;

    @Mock
    private LockManager lockManager;

    @InjectMocks
    private DoctorService doctorService;

    private SetOpenAppointmentTimesRequestDto requestDto;
    private Doctor doctor;
    private Appointment appointment;

    @BeforeEach
    public void setup() {
        doctor = new Doctor();
        doctor.setId(1L);

        requestDto = new SetOpenAppointmentTimesRequestDto();
        requestDto.setDoctorId(doctor.getId());
        requestDto.setStartTime(LocalDateTime.now());
        requestDto.setEndTime(LocalDateTime.now().plusHours(2));

        appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setStartTime(requestDto.getStartTime());
        appointment.setEndTime(requestDto.getEndTime());
    }

    @Test
    public void testSetOpenAppointmentTimes_ValidationException() {
        requestDto.setEndTime(requestDto.getStartTime().minusMinutes(30));
        assertThrows(ValidationException.class, () -> {
            doctorService.setOpenAppointmentTimes(requestDto);
        });
    }

    @Test
    public void testSetOpenAppointmentTimes_UserNotFoundException() {
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            doctorService.setOpenAppointmentTimes(requestDto);
        });
    }

    @Test
    public void testSetOpenAppointmentTimes_Success() throws ValidationException, UserNotFoundException {
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(generalServiceAssembler.convertNewAppointment(any(LocalDateTime.class), any(Doctor.class))).thenReturn(appointment);
        when(generalServiceAssembler.convertSetOpenAppointmentTimesResponseDto(anyInt())).thenReturn(new SetOpenAppointmentTimesResponseDto());

        SetOpenAppointmentTimesResponseDto response = doctorService.setOpenAppointmentTimes(requestDto);
        assertNotNull(response);
    }

    @Test
    public void testViewAllAppointments_Success() throws UserNotFoundException {
        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentRepository.findByDoctorId(anyLong())).thenReturn(appointments);
        when(generalServiceAssembler.convertViewAllAppointmentsResponseDto(anyList())).thenReturn(new ViewAllAppointmentsResponseDto());

        ViewAllAppointmentsResponseDto response = doctorService.viewAllAppointments(1L);
        assertNotNull(response);
    }

    @Test
    @Transactional
    public void testDeleteAppointment_NoAppointmentFoundException() {
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(anyLong())).thenReturn(lock);

        assertThrows(NoAppointmentFoundException.class, () -> {
            doctorService.deleteAppointment(1L, 1L);
        });

        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testDeleteAppointment_AppointmentIsTakenException() {
        appointment.setTaken(true);
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(anyLong())).thenReturn(lock);

        assertThrows(AppointmentIsTakenException.class, () -> {
            doctorService.deleteAppointment(1L, 1L);
        });

        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testDeleteAppointment_AccessDeniedException() {
        Doctor anotherDoctor = new Doctor();
        anotherDoctor.setId(2L);
        appointment.setDoctor(anotherDoctor);
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(anyLong())).thenReturn(lock);

        assertThrows(AccessDeniedException.class, () -> {
            doctorService.deleteAppointment(1L, 1L);
        });

        verify(lock).lock();
        verify(lock).unlock();
    }

    @Test
    @Transactional
    public void testDeleteAppointment_Success() throws NoAppointmentFoundException, AppointmentIsTakenException, AccessDeniedException {
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));
        Lock lock = mock(Lock.class);
        when(lockManager.getLock(anyLong())).thenReturn(lock);

        doctorService.deleteAppointment(1L, 1L);

        verify(appointmentRepository).delete(appointment);
        verify(lock).lock();
        verify(lock).unlock();
    }
}
