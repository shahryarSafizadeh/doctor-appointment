package com.blubank.doctorappointment.dto.exception;

import com.blubank.doctorappointment.dto.exception.enumeration.ErrorCode;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorType;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
public class AppointmentSystemException extends Exception {
    private ErrorCode errorCode;
    private ErrorType errorType;

    public AppointmentSystemException(String message, ErrorType errorType, ErrorCode errorCode) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    public AppointmentSystemException(String message) {
        super(message);
    }

    public AppointmentSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppointmentSystemException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public String getErrorType() {
        return ErrorType.GENERAL.getType();
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }
}
