package com.blubank.doctorappointment.persistence.repository;

import com.blubank.doctorappointment.persistence.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024 
 */

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByIsTakenFalseAndStartTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    Optional<Appointment> findByIdAndIsTakenFalse(Long id);
    List<Appointment> findByPatientId(Long patientId);
}
