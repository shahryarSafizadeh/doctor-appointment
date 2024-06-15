package com.blubank.doctorappointment.service.assembler;

import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesResponseDto;
import com.blubank.doctorappointment.dto.ViewAllAppointmentsResponseDto;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Doctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024
 */
@ExtendWith(MockitoExtension.class)
public class DoctorServiceAssemblerUTest {

    @InjectMocks
    private DoctorServiceAssembler doctorServiceAssembler;

    @Mock
    private GeneralServiceAssembler generalServiceAssembler;

    private static final Long DOCTOR_ID = 1L;
    private static final String DOCTOR_NAME = "Dr. Yegane";
    private static final boolean APPOINTMENT_TAKEN = false;
    private static final int APPOINTMENTS_COUNT = 5;
    private static final LocalDateTime START_TIME = LocalDateTime.now();
    private static final LocalDateTime END_TIME = START_TIME.plusMinutes(30);
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();

    private Doctor doctor;
    private Appointment appointment;

    @BeforeEach
    public void setup() {
        doctor = new Doctor();
        doctor.setId(DOCTOR_ID);
        doctor.setName(DOCTOR_NAME);

        appointment = new Appointment();
        appointment.setId(DOCTOR_ID);
        appointment.setDoctor(doctor);
        appointment.setTaken(APPOINTMENT_TAKEN);
        appointment.setDate(CURRENT_DATE);
        appointment.setStartTime(START_TIME);
        appointment.setEndTime(END_TIME);
    }

    @Test
    public void testConvertNewAppointment() {
        Appointment newAppointment = doctorServiceAssembler.convertNewAppointment(START_TIME, doctor);
        assertNotNull(newAppointment);
        assertEquals(doctor, newAppointment.getDoctor());
        assertFalse(newAppointment.isTaken());
        assertEquals(CURRENT_DATE.toLocalDate(), newAppointment.getDate().toLocalDate());
        assertEquals(START_TIME, newAppointment.getStartTime());
        assertEquals(END_TIME, newAppointment.getEndTime());
    }

    @Test
    public void testConvertSetOpenAppointmentTimesResponseDto() {
        SetOpenAppointmentTimesResponseDto responseDto = doctorServiceAssembler.convertSetOpenAppointmentTimesResponseDto(APPOINTMENTS_COUNT);
        assertNotNull(responseDto);
        assertEquals(APPOINTMENTS_COUNT, responseDto.getAppointmentsCount());
    }

    @Test
    public void testConvertViewAllAppointmentsResponseDto() {
        List<Appointment> appointments = Arrays.asList(appointment);
        ViewAllAppointmentsResponseDto expectedResponseDto = new ViewAllAppointmentsResponseDto();
        when(generalServiceAssembler.convertAppointmentsResponseDto(appointments, expectedResponseDto)).thenReturn(expectedResponseDto);
        ViewAllAppointmentsResponseDto responseDto = doctorServiceAssembler.convertViewAllAppointmentsResponseDto(appointments);
        assertNotNull(responseDto);
        verify(generalServiceAssembler).convertAppointmentsResponseDto(appointments, expectedResponseDto);
    }
}
