package com.blubank.doctorappointment.dto.exception;

import com.blubank.doctorappointment.dto.exception.enumeration.ErrorCode;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorType;


/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024 
 */
public class ValidationException extends AppointmentSystemException {

    public ValidationException(String message, ErrorType errorType, ErrorCode errorCode) {
        super(message, errorType, errorCode);
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String message, ErrorType errorType) {
        super(message, errorType);
    }

    @Override
    public String getErrorType() {
        return ErrorType.VALIDATION.getType();
    }
}
