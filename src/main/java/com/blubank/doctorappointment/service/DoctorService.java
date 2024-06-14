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
import com.blubank.doctorappointment.service.assembler.GeneralServiceAssembler;
import com.blubank.doctorappointment.util.LockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final GeneralServiceAssembler generalServiceAssembler;
    private final LockManager lockManager;

    public SetOpenAppointmentTimesResponseDto setOpenAppointmentTimes(SetOpenAppointmentTimesRequestDto request) throws
            ValidationException, UserNotFoundException {
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();
        int appointmentsCount = 0;
        if (endTime.isBefore(startTime)) {
            throw new ValidationException("Invalid time range.", ErrorType.VALIDATION, ErrorCode.INVALID_TIME_RANGE);
        }

        if (Duration.between(startTime, endTime).toMinutes() < 30) {
            return new SetOpenAppointmentTimesResponseDto();
        }

        while (startTime.plusMinutes(30).isBefore(endTime) || startTime.plusMinutes(30).isEqual(endTime)) {
            Doctor doctor = (Doctor) doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new UserNotFoundException("Doctor not found", ErrorType.GENERAL, ErrorCode.NO_DOCTOR_FOUND));
            Appointment appointment = generalServiceAssembler.convertNewAppointment(startTime, doctor);
            appointmentRepository.save(appointment);
            startTime = startTime.plusMinutes(30);
            appointmentsCount++;
        }
        return generalServiceAssembler.convertSetOpenAppointmentTimesResponseDto(appointmentsCount);
    }

    public ViewAllAppointmentsResponseDto viewAllAppointments(Long doctorId) throws UserNotFoundException {
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);
        return generalServiceAssembler.convertViewAllAppointmentsResponseDto(appointments);
    }

    @Transactional
    public void deleteAppointment(Long appointmentId, Long doctorId) throws NoAppointmentFoundException, AppointmentIsTakenException, AccessDeniedException {
        Lock lock = lockManager.getLock(appointmentId);
        lock.lock();
        try {
            Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
            if (!appointmentOptional.isPresent()) {
                throw new NoAppointmentFoundException("Appointment not found", ErrorType.GENERAL, ErrorCode.NO_APPOINTMENT_FOUND);
            }
            Appointment appointment = appointmentOptional.get();
            if (!appointment.getDoctor().getId().equals(doctorId)) {
                throw new AccessDeniedException("Doctor not authorized to delete this appointment");
            }
            if (appointment.isTaken()) {
                throw new AppointmentIsTakenException("Cannot delete a taken appointment", ErrorType.GENERAL, ErrorCode.APPOINTMENT_IS_TAKEN);
            }
            appointmentRepository.delete(appointment);
        } finally {
            lock.unlock();
            lockManager.removeLock(appointmentId);
        }
    }
}
