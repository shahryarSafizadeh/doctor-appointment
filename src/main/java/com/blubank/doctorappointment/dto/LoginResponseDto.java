package com.blubank.doctorappointment.dto;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024 
 */
@Data
public class LoginResponseDto {

    /**
     * token
     */
    private String token;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("token", token)
                .toString();
    }
}
