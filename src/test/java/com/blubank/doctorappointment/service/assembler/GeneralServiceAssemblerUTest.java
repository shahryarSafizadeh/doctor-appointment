package com.blubank.doctorappointment.service.assembler;


import com.blubank.doctorappointment.dto.*;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Doctor;
import com.blubank.doctorappointment.persistence.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
public class GeneralServiceAssemblerUTest {

    @InjectMocks
    private GeneralServiceAssembler generalServiceAssembler;

    private Doctor doctor;
    private Appointment appointment;
    private LocalDateTime startTime;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        doctor = new Doctor();
        doctor.setId(1L);
        startTime = LocalDateTime.now();

        appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setTaken(false);
        appointment.setDate(LocalDateTime.now());
        appointment.setStartTime(startTime);
        appointment.setEndTime(startTime.plusMinutes(30));
    }

    @Test
    public void testConvertNewAppointment() {
        Appointment newAppointment = generalServiceAssembler.convertNewAppointment(startTime, doctor);
        assertNotNull(newAppointment);
        assertEquals(doctor, newAppointment.getDoctor());
        assertEquals(startTime, newAppointment.getStartTime());
        assertEquals(startTime.plusMinutes(30), newAppointment.getEndTime());
        assertFalse(newAppointment.isTaken());
    }

    @Test
    public void testConvertSetOpenAppointmentTimesResponseDto() {
        int appointmentsCount = 5;
        SetOpenAppointmentTimesResponseDto responseDto = generalServiceAssembler.convertSetOpenAppointmentTimesResponseDto(appointmentsCount);
        assertNotNull(responseDto);
        assertEquals(appointmentsCount, responseDto.getAppointmentsCount());
    }

    @Test
    public void testConvertViewAllAppointmentsResponseDto_EmptyList() {
        List<Appointment> appointments = new ArrayList<>();
        ViewAllAppointmentsResponseDto responseDto = generalServiceAssembler.convertViewAllAppointmentsResponseDto(appointments);
        assertNotNull(responseDto);
        assertTrue(CollectionUtils.isEmpty(responseDto.getAppointments()));
    }

    @Test
    public void testConvertViewAllAppointmentsResponseDto_NonEmptyList() {
        List<Appointment> appointments = Arrays.asList(appointment);
        ViewAllAppointmentsResponseDto responseDto = generalServiceAssembler.convertViewAllAppointmentsResponseDto(appointments);
        assertNotNull(responseDto);
        assertFalse(CollectionUtils.isEmpty(responseDto.getAppointments()));
        assertEquals(1, responseDto.getAppointments().size());
    }

    @Test
    public void testConvertAppointmentBaseDto_TakenAppointment() {
        appointment.setTaken(true);
        Patient patient = new Patient();
        patient.setName("Shahryar khan");
        patient.setPhoneNumber("123456789");
        appointment.setPatient(patient);

        AppointmentBaseDto appointmentBaseDto = generalServiceAssembler.convertAppointmentBaseDto(appointment);
        assertTrue(appointmentBaseDto instanceof TakenAppointmentDto);
        TakenAppointmentDto takenAppointmentDto = (TakenAppointmentDto) appointmentBaseDto;
        assertEquals("Shahryar khan", takenAppointmentDto.getPatientName());
        assertEquals("123456789", takenAppointmentDto.getPatientPhoneNumber());
        assertEquals(appointment.getStartTime(), takenAppointmentDto.getStartTime());
    }

    @Test
    public void testConvertAppointmentBaseDto_NotTakenAppointment() {
        appointment.setTaken(false);
        AppointmentBaseDto appointmentBaseDto = generalServiceAssembler.convertAppointmentBaseDto(appointment);
        assertFalse(appointmentBaseDto instanceof TakenAppointmentDto);
        assertEquals(appointment.getStartTime(), appointmentBaseDto.getStartTime());
        assertEquals(appointment.getEndTime(), appointmentBaseDto.getEndTime());
    }

    @Test
    public void testConvertReserveAppointmentResponseDto() {
        ReserveAppointmentResponseDto responseDto = generalServiceAssembler.convertReserveAppointmentResponseDto(appointment);
        assertNotNull(responseDto);
        assertNotNull(responseDto.getReservedAppointment());
        assertEquals(appointment.getId(), responseDto.getReservedAppointment().getAppointmentId());
        assertEquals(appointment.isTaken(), responseDto.getReservedAppointment().isTaken());
        assertEquals(appointment.getStartTime(), responseDto.getReservedAppointment().getStartTime());
        assertEquals(appointment.getEndTime(), responseDto.getReservedAppointment().getEndTime());
        assertEquals(appointment.getDate(), responseDto.getReservedAppointment().getDate());
    }

    @Test
    public void testConvertToTakenAppointment() {
        GeneralServiceAssembler doctorServiceAssembler;
        AppointmentBaseDto appointmentBaseDto = generalServiceAssembler.convertToTakenAppointment(appointment);
        assertNotNull(appointmentBaseDto);
        assertEquals(appointment.getId(), appointmentBaseDto.getAppointmentId());
        assertEquals(appointment.isTaken(), appointmentBaseDto.isTaken());
        assertEquals(appointment.getStartTime(), appointmentBaseDto.getStartTime());
        assertEquals(appointment.getEndTime(), appointmentBaseDto.getEndTime());
        assertEquals(appointment.getDate(), appointmentBaseDto.getDate());
    }
}