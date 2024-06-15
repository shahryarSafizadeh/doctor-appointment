package com.blubank.doctorappointment.dto;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024 
 */
@Data
public class AllAppointmentsBaseDto {

    /**
     * list of all appointment
     */
    private List<AppointmentBaseDto> appointments;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("appointments", appointments)
                .toString();
    }
}