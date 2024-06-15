package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesRequestDto;
import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesResponseDto;
import com.blubank.doctorappointment.dto.ViewAllAppointmentsResponseDto;
import com.blubank.doctorappointment.dto.exception.*;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorCode;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorType;
import com.blubank.doctorappointment.persistence.entity.AppUser;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Doctor;
import com.blubank.doctorappointment.persistence.repository.AppUserRepository;
import com.blubank.doctorappointment.persistence.repository.AppointmentRepository;
import com.blubank.doctorappointment.persistence.repository.DoctorRepository;
import com.blubank.doctorappointment.service.assembler.DoctorServiceAssembler;
import com.blubank.doctorappointment.util.LockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
public class DoctorService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final AppUserRepository appUserRepository;
    private final DoctorServiceAssembler doctorServiceAssembler;
    private final LockManager lockManager;

    public SetOpenAppointmentTimesResponseDto setOpenAppointmentTimes(SetOpenAppointmentTimesRequestDto request) throws
            ValidationException, UserNotFoundException {

        validateTimeRange(request.getStartTime(), request.getEndTime());
        Long doctorId = getCurrentUserId();
        Doctor doctor = findDoctorById(doctorId);
        checkExistingAppointments(doctor, request.getStartTime(), request.getEndTime());
        int appointmentsCount = createAppointments(doctor, request.getStartTime(), request.getEndTime());

        return doctorServiceAssembler.convertSetOpenAppointmentTimesResponseDto(appointmentsCount);
    }

    public ViewAllAppointmentsResponseDto viewAllAppointments() throws UserNotFoundException {
        Long doctorId = getCurrentUserId();
        log.info("Viewing all appointments for doctorId: {}", doctorId);
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);
        return doctorServiceAssembler.convertViewAllAppointmentsResponseDto(appointments);
    }

    @Transactional
    public void deleteAppointment(Long appointmentId) throws NoAppointmentFoundException, AppointmentIsTakenException,
            AccessDeniedException, UserNotFoundException {
        Long doctorId = getCurrentUserId();
        log.info("Deleting appointment with id: {} for doctorId: {}", appointmentId, doctorId);

        Lock lock = lockManager.getLock(appointmentId);
        lock.lock();
        try {
            log.debug("Lock acquired for deleting appointment with id: {}", appointmentId);
            Appointment appointment = findAppointmentByDoctorAndId(doctorId, appointmentId);
            validateAppointmentForDeletion(appointment);
            deleteAppointmentFromRepository(appointmentId);
            log.info("Deleted appointment with id: {}", appointmentId);
        } finally {
            lock.unlock();
            lockManager.removeLock(appointmentId);
            log.debug("Lock released after deleting appointment with id: {}", appointmentId);
        }
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) throws ValidationException {
        if (endTime.isBefore(startTime)) {
            log.error("Invalid time range: endTime {} is before startTime {}", endTime, startTime);
            throw new ValidationException("Invalid time range.", ErrorType.VALIDATION, ErrorCode.INVALID_TIME_RANGE);
        }
        if (Duration.between(startTime, endTime).toMinutes() < 30) {
            log.info("Time range is less than 30 minutes. No appointments created.");
            throw new ValidationException("Time range is less than 30 minutes. No appointments created.", ErrorType.VALIDATION, ErrorCode.INVALID_TIME_RANGE);
        }
    }

    private Doctor findDoctorById(Long doctorId) throws UserNotFoundException {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new UserNotFoundException("Doctor not found", ErrorType.GENERAL, ErrorCode.NO_DOCTOR_FOUND));
    }

    private void checkExistingAppointments(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) throws ValidationException {
        List<Appointment> existingAppointments = appointmentRepository.findByDoctorAndStartTimeBetween(doctor, startTime, endTime);
        if (!existingAppointments.isEmpty()) {
            log.error("There are existing appointments in the specified time range for doctorId: {}", doctor.getId());
            throw new ValidationException("There are existing appointments in the specified time range.", ErrorType.VALIDATION, ErrorCode.EXISTING_APPOINTMENTS);
        }
    }

    private int createAppointments(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) {
        int appointmentsCount = 0;
        while (startTime.plusMinutes(30).isBefore(endTime) || startTime.plusMinutes(30).isEqual(endTime)) {
            Appointment appointment = doctorServiceAssembler.convertNewAppointment(startTime, doctor);
            appointmentRepository.save(appointment);
            log.debug("Created appointment for doctorId: {} at {}", doctor.getId(), startTime);
            startTime = startTime.plusMinutes(30);
            appointmentsCount++;
        }
        return appointmentsCount;
    }

    private Appointment findAppointmentByDoctorAndId(Long doctorId, Long appointmentId) throws NoAppointmentFoundException {
        return appointmentRepository.findByDoctorIdAndId(doctorId, appointmentId)
                .orElseThrow(() -> {
                    log.error("Appointment with id: {} not found for doctorId: {}", appointmentId, doctorId);
                    return new NoAppointmentFoundException("Appointment not found", ErrorType.GENERAL, ErrorCode.NO_APPOINTMENT_FOUND);
                });
    }

    private void validateAppointmentForDeletion(Appointment appointment) throws AppointmentIsTakenException {
        if (appointment.isTaken()) {
            log.error("Cannot delete a taken appointment with id: {}", appointment.getId());
            throw new AppointmentIsTakenException("Cannot delete a taken appointment", ErrorType.GENERAL, ErrorCode.APPOINTMENT_IS_TAKEN);
        }
    }

    private void deleteAppointmentFromRepository(Long appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }

    private Long getCurrentUserId() throws UserNotFoundException {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Doctor not found : " + username,
                        ErrorType.AUTHENTICATION, ErrorCode.NO_DOCTOR_FOUND));
        return user.getId();
    }
}
