package com.blubank.doctorappointment.service.assembler;


import com.blubank.doctorappointment.dto.AppointmentBaseDto;
import com.blubank.doctorappointment.dto.ViewAllAppointmentsResponseDto;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024 
 */
@ExtendWith(MockitoExtension.class)
public class GeneralServiceAssemblerUTest {

    @InjectMocks
    private GeneralServiceAssembler generalServiceAssembler;

    private static final Long APPOINTMENT_ID = 1L;
    private static final boolean APPOINTMENT_TAKEN = true;
    private static final boolean APPOINTMENT_NOT_TAKEN = false;
    private static final String PATIENT_PHONE_NUMBER = "123456789";
    private static final String PATIENT_NAME = "Shahryar";
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
    public void testConvertAppointmentBaseDto_Taken() {
        AppointmentBaseDto appointmentBaseDto = generalServiceAssembler.convertAppointmentBaseDto(appointment);
        assertNotNull(appointmentBaseDto);
        assertEquals(APPOINTMENT_ID, appointmentBaseDto.getAppointmentId());
        assertEquals(APPOINTMENT_TAKEN, appointmentBaseDto.isTaken());
        assertEquals(APPOINTMENT_DATE, appointmentBaseDto.getDate());
        assertEquals(START_TIME, appointmentBaseDto.getStartTime());
        assertEquals(END_TIME, appointmentBaseDto.getEndTime());
        assertEquals(PATIENT_PHONE_NUMBER, appointmentBaseDto.getPatientPhoneNumber());
        assertEquals(PATIENT_NAME, appointmentBaseDto.getPatientName());
    }

    @Test
    public void testConvertAppointmentBaseDto_NotTaken() {
        appointment.setTaken(APPOINTMENT_NOT_TAKEN);
        AppointmentBaseDto appointmentBaseDto = generalServiceAssembler.convertAppointmentBaseDto(appointment);
        assertNotNull(appointmentBaseDto);
        assertEquals(APPOINTMENT_ID, appointmentBaseDto.getAppointmentId());
        assertEquals(APPOINTMENT_NOT_TAKEN, appointmentBaseDto.isTaken());
        assertEquals(APPOINTMENT_DATE, appointmentBaseDto.getDate());
        assertEquals(START_TIME, appointmentBaseDto.getStartTime());
        assertEquals(END_TIME, appointmentBaseDto.getEndTime());
        assertNull(appointmentBaseDto.getPatientPhoneNumber());
        assertNull(appointmentBaseDto.getPatientName());
    }

    @Test
    public void testConvertAppointmentsResponseDto() {
        List<Appointment> appointments = Arrays.asList(appointment);
        ViewAllAppointmentsResponseDto responseDto = new ViewAllAppointmentsResponseDto();
        ViewAllAppointmentsResponseDto result = generalServiceAssembler.convertAppointmentsResponseDto(appointments, responseDto);
        assertNotNull(result);
        assertNotNull(result.getAppointments());
        assertEquals(1, result.getAppointments().size());
        AppointmentBaseDto appointmentBaseDto = result.getAppointments().get(0);
        assertEquals(APPOINTMENT_ID, appointmentBaseDto.getAppointmentId());
        assertEquals(APPOINTMENT_TAKEN, appointmentBaseDto.isTaken());
        assertEquals(APPOINTMENT_DATE, appointmentBaseDto.getDate());
        assertEquals(START_TIME, appointmentBaseDto.getStartTime());
        assertEquals(END_TIME, appointmentBaseDto.getEndTime());
        assertEquals(PATIENT_PHONE_NUMBER, appointmentBaseDto.getPatientPhoneNumber());
        assertEquals(PATIENT_NAME, appointmentBaseDto.getPatientName());
    }
}
