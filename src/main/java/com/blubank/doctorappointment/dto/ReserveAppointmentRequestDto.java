package com.blubank.doctorappointment.dto;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@Data
public class ReserveAppointmentRequestDto {

    /**
     * patient ID
     */
    @NotNull
    private Long patientId;

    /**
     * appointment ID
     */
    @NotNull
    private Long appointmentId;

    /**
     * phone number
     */
    @NotEmpty
    private String phoneNumber;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("patientId", patientId)
                .append("appointmentId", appointmentId)
                .append("phoneNumber", phoneNumber)
                .toString();
    }
}
