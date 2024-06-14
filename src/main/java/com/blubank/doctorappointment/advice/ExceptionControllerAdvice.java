package com.blubank.doctorappointment.advice;

import com.blubank.doctorappointment.dto.SystemFailureResponse;
import com.blubank.doctorappointment.dto.exception.AppointmentSystemException;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorCode;
import com.blubank.doctorappointment.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@RestControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    private static final Map<String, HttpStatus> statusCodes = new HashMap<>();

    {
        statusCodes.put(ErrorCode.INVALID_TIME_RANGE.getCode(), HttpStatus.BAD_REQUEST);                           //400
        statusCodes.put(ErrorCode.NO_APPOINTMENT_FOUND.getCode(), HttpStatus.NOT_FOUND);                           //404
        statusCodes.put(ErrorCode.APPOINTMENT_IS_TAKEN.getCode(), HttpStatus.NOT_ACCEPTABLE);                      //406
    }

    @ExceptionHandler(AppointmentSystemException.class)
    public ResponseEntity<SystemFailureResponse> exception(AppointmentSystemException appointmentSystemException) {
        SystemFailureResponse failureResponse = ResponseUtil.createSystemFailureResponse(appointmentSystemException);
        return new ResponseEntity<>(failureResponse, getHttpStatusCode(appointmentSystemException.getErrorCode()));
    }

    private HttpStatus getHttpStatusCode(String errorCode) {
        return statusCodes.getOrDefault(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
