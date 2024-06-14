package com.blubank.doctorappointment.persistence.repository;

import com.blubank.doctorappointment.persistence.entity.Doctor;
import com.blubank.doctorappointment.persistence.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024 
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {

}
