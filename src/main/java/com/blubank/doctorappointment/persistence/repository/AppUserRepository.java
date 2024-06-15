package com.blubank.doctorappointment.persistence.repository;

import com.blubank.doctorappointment.persistence.entity.AppUser;
import com.blubank.doctorappointment.persistence.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024 
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long>{
    Optional<AppUser> findByUsername(String username);
}
