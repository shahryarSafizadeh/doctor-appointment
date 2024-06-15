package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesRequestDto;
import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesResponseDto;
import com.blubank.doctorappointment.dto.ViewAllAppointmentsResponseDto;
import com.blubank.doctorappointment.dto.exception.*;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorCode;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorType;
import com.blubank.doctorappointment.persistence.entity.Appointment;
import com.blubank.doctorappointment.persistence.entity.Doctor;
import com.blubank.doctorappointment.persistence.repository.AppointmentRepository;
import com.blubank.doctorappointment.persistence.repository.DoctorRepository;
import com.blubank.doctorappointment.service.assembler.DoctorServiceAssembler;
import com.blubank.doctorappointment.util.LockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
@Slf4j
public class DoctorService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorServiceAssembler doctorServiceAssembler;
    private final LockManager lockManager;

    public SetOpenAppointmentTimesResponseDto setOpenAppointmentTimes(SetOpenAppointmentTimesRequestDto request) throws
            ValidationException, UserNotFoundException {
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();
        int appointmentsCount = 0;
        if (endTime.isBefore(startTime)) {
            log.error("Invalid time range: endTime {} is before startTime {}", endTime, startTime);
            throw new ValidationException("Invalid time range.", ErrorType.VALIDATION, ErrorCode.INVALID_TIME_RANGE);
        }

        if (Duration.between(startTime, endTime).toMinutes() < 30) {
            log.info("Time range is less than 30 minutes. No appointments created.");
            return new SetOpenAppointmentTimesResponseDto();
        }
        Doctor doctor = (Doctor) doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new UserNotFoundException("Doctor not found", ErrorType.GENERAL, ErrorCode.NO_DOCTOR_FOUND));
        while (startTime.plusMinutes(30).isBefore(endTime) || startTime.plusMinutes(30).isEqual(endTime)) {
            Appointment appointment = doctorServiceAssembler.convertNewAppointment(startTime, doctor);
            appointmentRepository.save(appointment);
            log.debug("Created appointment for doctorId: {} at {}", doctor.getId(), startTime);
            startTime = startTime.plusMinutes(30);
            appointmentsCount++;
        }
        return doctorServiceAssembler.convertSetOpenAppointmentTimesResponseDto(appointmentsCount);
    }

    public ViewAllAppointmentsResponseDto viewAllAppointments(Long doctorId) throws UserNotFoundException {
        log.info("Viewing all appointments for doctorId: {}", doctorId);
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);
        return doctorServiceAssembler.convertViewAllAppointmentsResponseDto(appointments);
    }

    @Transactional
    public void deleteAppointment(Long appointmentId, Long doctorId) throws NoAppointmentFoundException, AppointmentIsTakenException, AccessDeniedException {
        log.info("Deleting appointment with id: {} for doctorId: {}", appointmentId, doctorId);
        Lock lock = lockManager.getLock(appointmentId);
        lock.lock();
        try {
            log.debug("Lock caught for deleting appointment with id: {}", appointmentId);
            Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
            if (!appointmentOptional.isPresent()) {
                log.error("Appointment with id: {} not found", appointmentId);
                throw new NoAppointmentFoundException("Appointment not found", ErrorType.GENERAL, ErrorCode.NO_APPOINTMENT_FOUND);
            }
            Appointment appointment = appointmentOptional.get();
            if (!appointment.getDoctor().getId().equals(doctorId)) {
                log.error("Doctor with id: {} not authorized to delete appointment with id: {}", doctorId, appointmentId);
                throw new AccessDeniedException("Doctor not authorized to delete this appointment");
            }
            if (appointment.isTaken()) {
                log.error("Cannot delete a taken appointment with id: {}", appointmentId);
                throw new AppointmentIsTakenException("Cannot delete a taken appointment", ErrorType.GENERAL, ErrorCode.APPOINTMENT_IS_TAKEN);
            }
            appointmentRepository.delete(appointment);
            log.info("Deleted appointment with id: {}", appointmentId);
        } finally {
            lock.unlock();
            lockManager.removeLock(appointmentId);
            log.debug("Lock released after deleting appointment with id: {}", appointmentId);
        }
    }
}
