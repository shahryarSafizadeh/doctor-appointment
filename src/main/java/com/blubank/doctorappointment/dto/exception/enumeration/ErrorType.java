package com.blubank.doctorappointment.dto.exception.enumeration;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
public enum ErrorType {
    /**
     * Authentication error.
     */
    AUTHENTICATION("auth"),

    /**
     * Validation error.
     */
    VALIDATION("validation"),

    /**
     * General error.
     */
    GENERAL("general"),

    /**
     * Operation denied error.
     */
    OPERATION_DENIED("operation denied");

    private final String type;

    ErrorType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}