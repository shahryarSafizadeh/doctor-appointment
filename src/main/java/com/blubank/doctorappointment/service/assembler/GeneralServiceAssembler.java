package com.blubank.doctorappointment.service.assembler;

import com.blubank.doctorappointment.dto.AllAppointmentsBaseDto;
import com.blubank.doctorappointment.dto.AppointmentBaseDto;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@Component
public class GeneralServiceAssembler {

    public <T extends AllAppointmentsBaseDto> T convertAppointmentsResponseDto(List<Appointment> appointments, T responseDto) {
        List<AppointmentBaseDto> appointmentBaseDtos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(appointments)) {
            for (Appointment appointment : appointments) {
                AppointmentBaseDto appointmentBaseDto = convertAppointmentBaseDto(appointment);
                appointmentBaseDtos.add(appointmentBaseDto);
            }
        }
        responseDto.setAppointments(appointmentBaseDtos);
        return responseDto;
    }

    public AppointmentBaseDto convertAppointmentBaseDto(Appointment appointment) {
        AppointmentBaseDto appointmentBaseDto = new AppointmentBaseDto();
        appointmentBaseDto.setAppointmentId(appointment.getId());
        appointmentBaseDto.setTaken(appointment.isTaken());
        appointmentBaseDto.setDate(appointment.getDate());
        appointmentBaseDto.setStartTime(appointment.getStartTime());
        appointmentBaseDto.setEndTime(appointment.getEndTime());
        appointmentBaseDto.setPatientPhoneNumber(appointment.isTaken() ? appointment.getPatient().getPhoneNumber() : null);
        appointmentBaseDto.setPatientName(appointment.isTaken() ? appointment.getPatient().getName() : null);
        return appointmentBaseDto;
    }
}
