package com.blubank.doctorappointment.dto;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@Data
public class SystemFailureResponse {

    /**
     * message
     */
    private String message;

    /**
     * error type
     */
    private String errorType;

    /**
     * error code
     */
    private String errorCode;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("message", message)
                .append("errorType", errorType)
                .append("errorCode", errorCode)
                .toString();
    }
}