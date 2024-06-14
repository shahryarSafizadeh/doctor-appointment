package com.blubank.doctorappointment.dto;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@Data
public class AppointmentBaseDto {

    /**
     * ID of Appointment
     */
    private Long appointmentId;

    /**
     * if the appointment is taken
     */
    private boolean isTaken;

    /**
     * start time of the appointment
     */
    private LocalDateTime startTime;

    /**
     * end time of the appointment
     */
    private LocalDateTime endTime;

    /**
     * date of the appointment
     */
    private LocalDateTime date;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("appointmentId", appointmentId)
                .append("isTaken", isTaken)
                .append("startTime", startTime)
                .append("endTime", endTime)
                .append("date", date)
                .toString();
    }
}
