package com.blubank.doctorappointment.dto.exception.enumeration;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
public enum ErrorCode {

    /**
     * provided time range is invalid
     */
    INVALID_TIME_RANGE("01"),

    /**
     * The appointment is already taken
     */
    APPOINTMENT_IS_TAKEN("02"),

    /**
     * No appointment was found
     */
    NO_APPOINTMENT_FOUND("03"),

    /**
     * No doctor was found
     */
    NO_DOCTOR_FOUND("04"),
    NO_PATIENT_FOUND("05");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public static ErrorCode getByCode(String code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode().equals(code) || String.valueOf(Integer.parseInt(errorCode.getCode())).equals(code)) {
                return errorCode;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }
}
