package com.blubank.doctorappointment.dto;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@Data
public class SetOpenAppointmentTimesRequestDto {

    /**
     * doctor id
     */
    @NotNull
    private Long doctorId;

    /**
     * start time of appointment
     */
    @NotNull
    private LocalDateTime startTime;

    /**
     * end time of appointment
     */
    @NotNull
    private LocalDateTime endTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("doctorId", doctorId)
                .append("startTime", startTime)
                .append("endTime", endTime)
                .toString();
    }
}
