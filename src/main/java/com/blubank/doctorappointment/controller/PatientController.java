package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.dto.ReserveAppointmentRequestDto;
import com.blubank.doctorappointment.dto.ReserveAppointmentResponseDto;
import com.blubank.doctorappointment.dto.ViewAllAppointmentsResponseDto;
import com.blubank.doctorappointment.dto.exception.AppointmentIsTakenException;
import com.blubank.doctorappointment.dto.exception.NoAppointmentFoundException;
import com.blubank.doctorappointment.dto.exception.UserNotFoundException;
import com.blubank.doctorappointment.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024 
 */
@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    /**
     * Retrieves all open appointments for a specific date.
     *
     * @param date the date for which to retrieve open appointments, in the format "yyyy-MM-dd"
     * @return a {@link ViewAllAppointmentsResponseDto} containing the details of all open appointments for the specified date
     */
    @GetMapping(value = "/appointment/all",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ViewAllAppointmentsResponseDto viewAllOpenAppointments(@RequestParam String date) {
        return patientService.viewAllOpenAppointments(date);
    }

    /**
     * Reserves an appointment for a patient.
     *
     * @param request the {@link ReserveAppointmentRequestDto} containing the details of the appointment to be reserved
     * @return a {@link ReserveAppointmentResponseDto} containing the details of the reserved appointment
     * @throws UserNotFoundException if the specified patient is not found
     * @throws NoAppointmentFoundException if the specified appointment is not found
     * @throws AppointmentIsTakenException if the specified appointment has already been taken
     */
    @PostMapping(value = "/appointment/reservation",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ReserveAppointmentResponseDto reserveAppointment(@RequestBody ReserveAppointmentRequestDto request)
            throws UserNotFoundException, NoAppointmentFoundException, AppointmentIsTakenException {
        return patientService.reserveAppointment(request);
    }

    /**
     * Retrieves all personal appointments for a specific patient.
     *
     * @param patientId the ID of the patient whose appointments are to be retrieved
     * @return a {@link ViewAllAppointmentsResponseDto} containing the details of all appointments for the specified patient
     */
    @GetMapping(value = "appointment",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ViewAllAppointmentsResponseDto viewPersonalAppointments(@RequestParam Long patientId) {
        return patientService.viewPersonalAppointments(patientId);
    }
}