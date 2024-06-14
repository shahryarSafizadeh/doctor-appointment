package com.blubank.doctorappointment.dto.exception;

import com.blubank.doctorappointment.dto.exception.enumeration.ErrorCode;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorType;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024 log
 */
public class NoAppointmentFoundException extends AppointmentSystemException {

    public NoAppointmentFoundException(String message, ErrorType errorType, ErrorCode errorCode) {
        super(message, errorType, errorCode);
    }

    public NoAppointmentFoundException(String message) {
        super(message);
    }

    public NoAppointmentFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAppointmentFoundException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}
