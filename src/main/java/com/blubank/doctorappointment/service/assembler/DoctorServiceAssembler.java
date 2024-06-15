package com.blubank.doctorappointment.service.assembler;

import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesResponseDto;
import com.blubank.doctorappointment.dto.ViewAllAppointmentsResponseDto;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Doctor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024 
 */
@Component
@RequiredArgsConstructor
public class DoctorServiceAssembler {

    private final GeneralServiceAssembler generalServiceAssembler;

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
        return generalServiceAssembler.convertAppointmentsResponseDto(appointments,new ViewAllAppointmentsResponseDto());
    }
}
