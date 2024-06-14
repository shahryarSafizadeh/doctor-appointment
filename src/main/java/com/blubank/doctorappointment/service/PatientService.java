package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.dto.ReserveAppointmentRequestDto;
import com.blubank.doctorappointment.dto.ReserveAppointmentResponseDto;
import com.blubank.doctorappointment.dto.ViewAllAppointmentsResponseDto;
import com.blubank.doctorappointment.dto.exception.AppointmentIsTakenException;
import com.blubank.doctorappointment.dto.exception.NoAppointmentFoundException;
import com.blubank.doctorappointment.dto.exception.UserNotFoundException;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorCode;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorType;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Patient;
import com.blubank.doctorappointment.persistence.repository.AppointmentRepository;
import com.blubank.doctorappointment.persistence.repository.PatientRepository;
import com.blubank.doctorappointment.service.assembler.GeneralServiceAssembler;
import com.blubank.doctorappointment.util.LockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024 
 */
@Service
@RequiredArgsConstructor
public class PatientService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final GeneralServiceAssembler generalServiceAssembler;
    private final LockManager lockManager;

    public ViewAllAppointmentsResponseDto viewAllOpenAppointments(String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
        List<Appointment> appointmentList = appointmentRepository.findByIsTakenFalseAndStartTimeBetween(startOfDay, endOfDay);
        return generalServiceAssembler.convertViewAllAppointmentsResponseDto(appointmentList);
    }

    @Transactional
    public ReserveAppointmentResponseDto reserveAppointment(ReserveAppointmentRequestDto request) throws NoAppointmentFoundException,
            AppointmentIsTakenException, UserNotFoundException {
        Lock lock = lockManager.getLock(request.getAppointmentId());
        lock.lock();
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findByIdAndIsTakenFalse(request.getAppointmentId());
            if (!appointmentOpt.isPresent()) {
                throw new NoAppointmentFoundException("Appointment not found", ErrorType.GENERAL, ErrorCode.NO_APPOINTMENT_FOUND);
            }
            Appointment appointment = appointmentOpt.get();
            if (appointment.isTaken()) {
                throw new AppointmentIsTakenException("Cannot delete a taken appointment", ErrorType.GENERAL, ErrorCode.APPOINTMENT_IS_TAKEN);
            }
            Patient patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new UserNotFoundException("Patient not found", ErrorType.GENERAL, ErrorCode.NO_PATIENT_FOUND));
            appointment.setPatient(patient);
            appointment.setTaken(true);
            appointmentRepository.save(appointment);
            return generalServiceAssembler.convertReserveAppointmentResponseDto(appointment);
        } finally {
            lock.unlock();
            lockManager.removeLock(request.getAppointmentId());
        }
    }

    public ViewAllAppointmentsResponseDto viewPersonalAppointments(Long patientId) {
        List<Appointment> appointmentList =  appointmentRepository.findByPatientId(patientId);
        return generalServiceAssembler.convertViewAllAppointmentsResponseDto(appointmentList);
    }
}