package com.blubank.doctorappointment.dto.exception;

import com.blubank.doctorappointment.dto.exception.enumeration.ErrorCode;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorType;


/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024 
 */
public class UserNotFoundException extends AppointmentSystemException {

    public UserNotFoundException(String message, ErrorType errorType, ErrorCode errorCode) {
        super(message, errorType, errorCode);
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}
