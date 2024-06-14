package com.blubank.doctorappointment.dto;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@Data
public class SetOpenAppointmentTimesResponseDto {

    /**
     * number of appointments that have been set
     */
    private int appointmentsCount;

    public int getAppointmentsCount() {
        return appointmentsCount;
    }

    public void setAppointmentsCount(int appointmentsCount) {
        this.appointmentsCount = appointmentsCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("appointmentsCount", appointmentsCount)
                .toString();
    }
}
