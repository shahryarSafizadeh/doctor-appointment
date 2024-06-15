package com.blubank.doctorappointment.dto;

import com.blubank.doctorappointment.persistence.entity.enumeration.UserRole;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024 
 */
@Data
public class RegisterRequestDto {

    /**
     * username
     */
    @NotEmpty
    private String username;

    /**
     * password
     */
    @NotEmpty
    private String password;

    /**
     * name
     */
    @NotEmpty
    private String name;

    /**
     * phone number
     */
    @NotEmpty
    private String phoneNumber;

    /**
     * user role
     */
    @NotNull
    private UserRole userRole;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("username", username)
                .append("password", password)
                .append("name", name)
                .append("phoneNumber", phoneNumber)
                .append("userRole", userRole)
                .toString();
    }
}
