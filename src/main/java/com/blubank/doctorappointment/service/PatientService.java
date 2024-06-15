package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.dto.ReserveAppointmentRequestDto;
import com.blubank.doctorappointment.dto.ReserveAppointmentResponseDto;
import com.blubank.doctorappointment.dto.ViewAllOpenAppointmentResponseDto;
import com.blubank.doctorappointment.dto.ViewPersonalAppointmentsResponseDto;
import com.blubank.doctorappointment.dto.exception.AppointmentIsTakenException;
import com.blubank.doctorappointment.dto.exception.NoAppointmentFoundException;
import com.blubank.doctorappointment.dto.exception.UserNotFoundException;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorCode;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorType;
import com.blubank.doctorappointment.persistence.entity.AppUser;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Patient;
import com.blubank.doctorappointment.persistence.repository.AppUserRepository;
import com.blubank.doctorappointment.persistence.repository.AppointmentRepository;
import com.blubank.doctorappointment.persistence.repository.PatientRepository;
import com.blubank.doctorappointment.service.assembler.GeneralServiceAssembler;
import com.blubank.doctorappointment.service.assembler.PatientServiceAssembler;
import com.blubank.doctorappointment.util.LockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final AppointmentRepository appointmentRepository;
    private final AppUserRepository appUserRepository;
    private final PatientRepository patientRepository;
    private final GeneralServiceAssembler generalServiceAssembler;
    private final PatientServiceAssembler patientServiceAssembler;
    private final LockManager lockManager;

    public ViewAllOpenAppointmentResponseDto viewAllOpenAppointments(String date) {
        log.info("Viewing all open appointments for date: {}", date);
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
        List<Appointment> appointmentList = appointmentRepository.findByIsTakenFalseAndStartTimeBetween(startOfDay, endOfDay);
        log.debug("Found {} open appointments for date: {}", appointmentList.size(), date);
        return patientServiceAssembler.convertViewAllOpenAppointmentsResponseDto(appointmentList);
    }

    @Transactional
    public ReserveAppointmentResponseDto reserveAppointment(ReserveAppointmentRequestDto request) throws NoAppointmentFoundException,
            AppointmentIsTakenException, UserNotFoundException {
        Long patientId = getCurrentUserId();
        log.info("Reserving appointment with id: {} for patientId: {}", request.getAppointmentId(), patientId);

        Lock lock = lockManager.getLock(request.getAppointmentId());
        lock.lock();
        try {
            log.debug("Lock acquired for reserving appointment with id: {}", request.getAppointmentId());
            Appointment appointment = findAvailableAppointment(request.getAppointmentId());
            validateAppointmentForReservation(appointment);
            Patient patient = findPatientById(patientId);
            reserveAppointmentForPatient(appointment, patient);
            log.info("Reserved appointment with id: {} for patientId: {}", request.getAppointmentId(), patientId);
            return patientServiceAssembler.convertReserveAppointmentResponseDto(appointment);
        } finally {
            lock.unlock();
            lockManager.removeLock(request.getAppointmentId());
            log.debug("Lock released after reserving appointment with id: {}", request.getAppointmentId());
        }
    }

    public ViewPersonalAppointmentsResponseDto viewPersonalAppointments() throws UserNotFoundException {
        Long patientId = getCurrentUserId();
        List<Appointment> appointmentList = appointmentRepository.findByPatientId(patientId);
        return patientServiceAssembler.convertViewPersonalAppointmentsResponseDto(appointmentList);
    }

    private Appointment findAvailableAppointment(Long appointmentId) throws NoAppointmentFoundException {
        return appointmentRepository.findByIdAndIsTakenFalse(appointmentId)
                .orElseThrow(() -> {
                    log.error("Appointment with id: {} not found", appointmentId);
                    return new NoAppointmentFoundException("Appointment not found", ErrorType.GENERAL, ErrorCode.NO_APPOINTMENT_FOUND);
                });
    }

    private void validateAppointmentForReservation(Appointment appointment) throws AppointmentIsTakenException {
        if (appointment.isTaken()) {
            log.error("Appointment with id: {} is already taken", appointment.getId());
            throw new AppointmentIsTakenException("Cannot reserve a taken appointment", ErrorType.GENERAL, ErrorCode.APPOINTMENT_IS_TAKEN);
        }
    }

    private Patient findPatientById(Long patientId) throws UserNotFoundException {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("Patient with id: {} not found", patientId);
                    return new UserNotFoundException("Patient not found", ErrorType.GENERAL, ErrorCode.NO_PATIENT_FOUND);
                });
    }

    private void reserveAppointmentForPatient(Appointment appointment, Patient patient) {
        appointment.setPatient(patient);
        appointment.setTaken(true);
        appointmentRepository.save(appointment);
    }

    private Long getCurrentUserId() throws UserNotFoundException {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() ->  new UserNotFoundException("Patient not found : " + username,
                        ErrorType.AUTHENTICATION, ErrorCode.NO_PATIENT_FOUND));
        return user.getId();
    }
}
