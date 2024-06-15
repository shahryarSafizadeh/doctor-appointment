package com.blubank.doctorappointment.util;

import com.blubank.doctorappointment.dto.SystemFailureResponse;
import com.blubank.doctorappointment.dto.exception.AppointmentSystemException;
import com.blubank.doctorappointment.dto.exception.enumeration.ErrorType;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
public class ResponseUtil {

    public static SystemFailureResponse createSystemFailureResponse(AppointmentSystemException appointmentSystemException) {
        SystemFailureResponse systemFailureResponse = new SystemFailureResponse();
        systemFailureResponse.setMessage(appointmentSystemException.getMessage());
        systemFailureResponse.setErrorType(appointmentSystemException.getErrorType());
        systemFailureResponse.setErrorCode(appointmentSystemException.getErrorCode());
        return systemFailureResponse;
    }

    public static SystemFailureResponse createSystemFailureResponse(Throwable appointmentSystemException) {
        SystemFailureResponse systemFailureResponse = new SystemFailureResponse();
        systemFailureResponse.setMessage(appointmentSystemException.getMessage());
        systemFailureResponse.setErrorType(ErrorType.GENERAL.getType());
        return systemFailureResponse;
    }
}
