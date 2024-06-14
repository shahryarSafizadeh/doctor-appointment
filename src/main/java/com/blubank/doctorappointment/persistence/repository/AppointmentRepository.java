package com.blubank.doctorappointment.persistence.repository;

import com.blubank.doctorappointment.persistence.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024 
 */

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorId(Long doctorId);
}
