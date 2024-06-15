package com.blubank.doctorappointment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.FileSystemResource;

@SpringBootApplication
@Slf4j
public class DoctorAppointmentApplication {

    public static void main(String[] args) {
        if (new FileSystemResource("config/logback.xml").getFile().exists()) {
            System.setProperty("logging.config", "file:config/logback.xml");
        } else {
            System.setProperty("logging.config", "classpath:config/logback.xml");
        }
        SpringApplicationBuilder app = new SpringApplicationBuilder(DoctorAppointmentApplication.class)
                .web(WebApplicationType.SERVLET);
        app.run();
    }
}
