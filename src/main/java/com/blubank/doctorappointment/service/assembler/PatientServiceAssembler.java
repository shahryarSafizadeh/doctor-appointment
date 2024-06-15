package com.blubank.doctorappointment.service.assembler;

import com.blubank.doctorappointment.dto.AppointmentBaseDto;
import com.blubank.doctorappointment.dto.ReserveAppointmentResponseDto;
import com.blubank.doctorappointment.dto.ViewAllOpenAppointmentResponseDto;
import com.blubank.doctorappointment.dto.ViewPersonalAppointmentsResponseDto;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024 
 */
@Component
@RequiredArgsConstructor
public class PatientServiceAssembler {
    private final GeneralServiceAssembler generalServiceAssembler;

    public ViewAllOpenAppointmentResponseDto convertViewAllOpenAppointmentsResponseDto(List<Appointment> appointments) {
        return generalServiceAssembler.convertAppointmentsResponseDto(appointments, new ViewAllOpenAppointmentResponseDto());
    }

    public ViewPersonalAppointmentsResponseDto convertViewPersonalAppointmentsResponseDto(List<Appointment> appointments) {
        return generalServiceAssembler.convertAppointmentsResponseDto(appointments, new ViewPersonalAppointmentsResponseDto());
    }

    public ReserveAppointmentResponseDto convertReserveAppointmentResponseDto(Appointment appointment) {
        ReserveAppointmentResponseDto responseDto = new ReserveAppointmentResponseDto();
        AppointmentBaseDto appointmentBaseDto = generalServiceAssembler.convertAppointmentBaseDto(appointment);
        responseDto.setReservedAppointment(appointmentBaseDto);
        return responseDto;
    }
}
