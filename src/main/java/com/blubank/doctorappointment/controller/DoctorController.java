package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesRequestDto;
import com.blubank.doctorappointment.dto.SetOpenAppointmentTimesResponseDto;
import com.blubank.doctorappointment.dto.ViewAllAppointmentsResponseDto;
import com.blubank.doctorappointment.dto.exception.*;
import com.blubank.doctorappointment.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    /**
     * Defines open appointment times for a doctor.
     *
     * @param request the {@link SetOpenAppointmentTimesRequestDto}
     * @return a {@link SetOpenAppointmentTimesResponseDto}
     * @throws UserNotFoundException if the user (doctor) is not found.
     * @throws ValidationException   if the provided appointment time range is invalid.
     */
    @PostMapping(value = "/appointment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SetOpenAppointmentTimesResponseDto setOpenAppointmentTimes(@RequestBody SetOpenAppointmentTimesRequestDto request)
            throws UserNotFoundException, ValidationException {
        return doctorService.setOpenAppointmentTimes(request);
    }

    /**
     * Retrieves all open appointments for a specific doctor.
     *
     * @param doctorId the ID of the doctor whose open appointments are to be retrieved
     * @return a {@link ViewAllAppointmentsResponseDto} containing the details of all open appointments
     * @throws UserNotFoundException if the user (doctor) is not found.
     */
    @GetMapping(value = "/appointment",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ViewAllAppointmentsResponseDto viewAllAppointments(@RequestParam Long doctorId) throws UserNotFoundException {
        return doctorService.viewAllAppointments(doctorId);
    }

    /**
     * Deletes an appointment by its ID.
     *
     * @param appointmentId the ID of the appointment to be deleted
     * @param doctorId the ID of Doctor
     * @throws AppointmentIsTakenException       if the appointment is already taken
     * @throws NoAppointmentFoundException if no appointment is found with the provided ID
     * @throws AccessDeniedException             if the user (doctor) does not have access to the wanted appointment
     */
    @DeleteMapping(value = "/appointment/{appointmentId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteAppointment(@PathVariable Long appointmentId, @RequestParam Long doctorId) throws AppointmentIsTakenException,
            NoAppointmentFoundException, AccessDeniedException {
        doctorService.deleteAppointment(appointmentId, doctorId);
    }
}