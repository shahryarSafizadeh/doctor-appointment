package com.blubank.doctorappointment.dto.exception;

import com.blubank.doctorappointment.dto.exception.enumeration.ErrorCode;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorType;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024 
 */
public class AppointmentIsTakenException extends AppointmentSystemException {

    public AppointmentIsTakenException(String message, ErrorType errorType, ErrorCode errorCode) {
        super(message, errorType, errorCode);
    }

    public AppointmentIsTakenException(String message) {
        super(message);
    }

    public AppointmentIsTakenException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppointmentIsTakenException(String message, ErrorType errorType) {
        super(message, errorType);
    }

    @Override
    public String getErrorType() {
        return ErrorType.OPERATION_DENIED.getType();
    }
}
