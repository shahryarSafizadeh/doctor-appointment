package com.blubank.doctorappointment.service.assembler;

import com.blubank.doctorappointment.dto.*;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Doctor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@Component
public class GeneralServiceAssembler {

    public Appointment convertNewAppointment(LocalDateTime startTime, Doctor doctor) {
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setTaken(false);
        appointment.setDate(LocalDateTime.now());
        appointment.setStartTime(startTime);
        appointment.setEndTime(startTime.plusMinutes(30));
        return appointment;
    }

    public SetOpenAppointmentTimesResponseDto convertSetOpenAppointmentTimesResponseDto(int appointmentsCount) {
        SetOpenAppointmentTimesResponseDto responseDto = new SetOpenAppointmentTimesResponseDto();
        responseDto.setAppointmentsCount(appointmentsCount);
        return responseDto;
    }

    public ViewAllAppointmentsResponseDto convertViewAllAppointmentsResponseDto(List<Appointment> appointments) {
        ViewAllAppointmentsResponseDto viewAllAppointmentsResponseDto = new ViewAllAppointmentsResponseDto();
        List<AppointmentBaseDto> appointmentBaseDtos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(appointments)) {
            for (Appointment appointment : appointments) {
                AppointmentBaseDto appointmentBaseDto = convertAppointmentBaseDto(appointment);
                appointmentBaseDtos.add(appointmentBaseDto);
            }
        }
        viewAllAppointmentsResponseDto.setAppointments(appointmentBaseDtos);
        return viewAllAppointmentsResponseDto;
    }

    public AppointmentBaseDto convertAppointmentBaseDto(Appointment appointment) {
        if (appointment.isTaken()) {
            TakenAppointmentDto takenAppointmentDto = new TakenAppointmentDto();
            takenAppointmentDto.setPatientName(appointment.getPatient().getName());
            takenAppointmentDto.setPatientPhoneNumber(appointment.getPatient().getPhoneNumber());
            setCommonAppointmentFields(takenAppointmentDto, appointment);
            return takenAppointmentDto;
        } else {
            AppointmentBaseDto appointmentBaseDto = new AppointmentBaseDto();
            setCommonAppointmentFields(appointmentBaseDto, appointment);
            return appointmentBaseDto;
        }
    }

    public ReserveAppointmentResponseDto convertReserveAppointmentResponseDto(Appointment appointment) {
        ReserveAppointmentResponseDto responseDto = new ReserveAppointmentResponseDto();
        AppointmentBaseDto appointmentBaseDto = convertToTakenAppointment(appointment);
        responseDto.setReservedAppointment(appointmentBaseDto);
        return responseDto;
    }

    public AppointmentBaseDto convertToTakenAppointment(Appointment appointment) {
        AppointmentBaseDto appointmentBaseDto = new AppointmentBaseDto();
        appointmentBaseDto.setAppointmentId(appointment.getId());
        appointmentBaseDto.setTaken(appointment.isTaken());
        appointmentBaseDto.setStartTime(appointment.getStartTime());
        appointmentBaseDto.setEndTime(appointment.getEndTime());
        appointmentBaseDto.setDate(appointment.getDate());
        return appointmentBaseDto;
    }

    private void setCommonAppointmentFields(AppointmentBaseDto appointmentBaseDto, Appointment appointment) {
        appointmentBaseDto.setAppointmentId(appointment.getId());
        appointmentBaseDto.setDate(appointment.getDate());
        appointmentBaseDto.setStartTime(appointment.getStartTime());
        appointmentBaseDto.setEndTime(appointment.getEndTime());
    }
}
