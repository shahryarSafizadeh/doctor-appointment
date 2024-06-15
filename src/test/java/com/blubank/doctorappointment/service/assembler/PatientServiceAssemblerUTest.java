package com.blubank.doctorappointment.service.assembler;


import com.blubank.doctorappointment.dto.AppointmentBaseDto;
import com.blubank.doctorappointment.dto.ReserveAppointmentResponseDto;
import com.blubank.doctorappointment.dto.ViewAllOpenAppointmentResponseDto;
import com.blubank.doctorappointment.dto.ViewPersonalAppointmentsResponseDto;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024 
 */
@ExtendWith(MockitoExtension.class)
public class PatientServiceAssemblerUTest {

    @InjectMocks
    private PatientServiceAssembler patientServiceAssembler;

    @Mock
    private GeneralServiceAssembler generalServiceAssembler;

    private static final Long APPOINTMENT_ID = 1L;
    private static final boolean APPOINTMENT_TAKEN = true;
    private static final String PATIENT_PHONE_NUMBER = "123456789";
    private static final String PATIENT_NAME = "Shayan joon";
    private static final LocalDateTime START_TIME = LocalDateTime.now();
    private static final LocalDateTime END_TIME = START_TIME.plusMinutes(30);
    private static final LocalDateTime APPOINTMENT_DATE = START_TIME.toLocalDate().atStartOfDay();

    private Appointment appointment;
    private Patient patient;

    @BeforeEach
    public void setup() {
        patient = new Patient();
        patient.setName(PATIENT_NAME);
        patient.setPhoneNumber(PATIENT_PHONE_NUMBER);
        appointment = new Appointment();
        appointment.setId(APPOINTMENT_ID);
        appointment.setPatient(patient);
        appointment.setTaken(APPOINTMENT_TAKEN);
        appointment.setDate(APPOINTMENT_DATE);
        appointment.setStartTime(START_TIME);
        appointment.setEndTime(END_TIME);
    }

    @Test
    public void testConvertViewAllOpenAppointmentsResponseDto() {
        List<Appointment> appointments = Arrays.asList(appointment);
        ViewAllOpenAppointmentResponseDto expectedResponseDto = new ViewAllOpenAppointmentResponseDto();
        when(generalServiceAssembler.convertAppointmentsResponseDto(appointments, expectedResponseDto)).thenReturn(expectedResponseDto);
        ViewAllOpenAppointmentResponseDto responseDto = patientServiceAssembler.convertViewAllOpenAppointmentsResponseDto(appointments);
        assertNotNull(responseDto);
        verify(generalServiceAssembler).convertAppointmentsResponseDto(appointments, expectedResponseDto);
    }

    @Test
    public void testConvertViewPersonalAppointmentsResponseDto() {
        List<Appointment> appointments = Arrays.asList(appointment);
        ViewPersonalAppointmentsResponseDto expectedResponseDto = new ViewPersonalAppointmentsResponseDto();
        when(generalServiceAssembler.convertAppointmentsResponseDto(appointments, expectedResponseDto)).thenReturn(expectedResponseDto);
        ViewPersonalAppointmentsResponseDto responseDto = patientServiceAssembler.convertViewPersonalAppointmentsResponseDto(appointments);
        assertNotNull(responseDto);
        verify(generalServiceAssembler).convertAppointmentsResponseDto(appointments, expectedResponseDto);
    }

    @Test
    public void testConvertReserveAppointmentResponseDto() {
        AppointmentBaseDto appointmentBaseDto = new AppointmentBaseDto();
        appointmentBaseDto.setAppointmentId(APPOINTMENT_ID);
        appointmentBaseDto.setTaken(APPOINTMENT_TAKEN);
        appointmentBaseDto.setDate(APPOINTMENT_DATE);
        appointmentBaseDto.setStartTime(START_TIME);
        appointmentBaseDto.setEndTime(END_TIME);
        appointmentBaseDto.setPatientPhoneNumber(PATIENT_PHONE_NUMBER);
        appointmentBaseDto.setPatientName(PATIENT_NAME);
        when(generalServiceAssembler.convertAppointmentBaseDto(appointment)).thenReturn(appointmentBaseDto);
        ReserveAppointmentResponseDto responseDto = patientServiceAssembler.convertReserveAppointmentResponseDto(appointment);
        assertNotNull(responseDto);
        assertNotNull(responseDto.getReservedAppointment());
        assertEquals(APPOINTMENT_ID, responseDto.getReservedAppointment().getAppointmentId());
        assertEquals(APPOINTMENT_TAKEN, responseDto.getReservedAppointment().isTaken());
        assertEquals(APPOINTMENT_DATE.toLocalDate(), responseDto.getReservedAppointment().getDate().toLocalDate());
        assertEquals(START_TIME, responseDto.getReservedAppointment().getStartTime());
        assertEquals(END_TIME, responseDto.getReservedAppointment().getEndTime());
        assertEquals(PATIENT_PHONE_NUMBER, responseDto.getReservedAppointment().getPatientPhoneNumber());
        assertEquals(PATIENT_NAME, responseDto.getReservedAppointment().getPatientName());
    }
}