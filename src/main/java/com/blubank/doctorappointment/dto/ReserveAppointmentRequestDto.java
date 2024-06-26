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
     * appointment ID
     */
    @NotNull
    private Long appointmentId;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("appointmentId", appointmentId)
                .toString();
    }
}
