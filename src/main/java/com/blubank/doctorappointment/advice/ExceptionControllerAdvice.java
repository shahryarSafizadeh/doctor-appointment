package com.blubank.doctorappointment.advice;

import com.blubank.doctorappointment.dto.SystemFailureResponse;
import com.blubank.doctorappointment.dto.exception.*;
import com.blubank.doctorappointment.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@RestControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AppointmentSystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SystemFailureResponse appointmentSystemException(AppointmentSystemException appointmentSystemException) {
        return ResponseUtil.createSystemFailureResponse(appointmentSystemException);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public SystemFailureResponse accessDeniedException(AccessDeniedException accessDeniedException) {
        return ResponseUtil.createSystemFailureResponse(accessDeniedException);
    }

    @ExceptionHandler(AppointmentIsTakenException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public SystemFailureResponse appointmentIsTakenException(AppointmentIsTakenException appointmentIsTakenException) {
        return ResponseUtil.createSystemFailureResponse(appointmentIsTakenException);
    }

    @ExceptionHandler(NoAppointmentFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SystemFailureResponse noAppointmentFoundException(NoAppointmentFoundException noAppointmentFoundException) {
        return ResponseUtil.createSystemFailureResponse(noAppointmentFoundException);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SystemFailureResponse userNotFoundException(UserNotFoundException userNotFoundException) {
        return ResponseUtil.createSystemFailureResponse(userNotFoundException);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SystemFailureResponse validationException(ValidationException validationException) {
        return ResponseUtil.createSystemFailureResponse(validationException);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SystemFailureResponse throwable(Throwable appointmentSystemException) {
        return ResponseUtil.createSystemFailureResponse(appointmentSystemException);
    }
}
