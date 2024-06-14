package com.blubank.doctorappointment.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.blubank.doctorappointment.controller..*(..))")
    public void logRequest(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        log.info("Request Body: " + Arrays.toString(args));
    }

    @AfterReturning(pointcut = "servicesPointcut()", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            log.info("Response Body: " + responseEntity.getBody());
            log.info("Response Status: " + responseEntity.getStatusCode());
        } else {
            log.info("Response Body: " + result);
        }
    }

    @AfterThrowing(pointcut = "servicesPointcut()", throwing = "throwable")
    public void logException(Throwable throwable) {
        log.error("Exception caught: {}", throwable.getMessage(), throwable);
    }

    @Pointcut(value = "execution(* com.blubank.doctorappointment.controller..*(..))")
    private void servicesPointcut() {
    }
}
