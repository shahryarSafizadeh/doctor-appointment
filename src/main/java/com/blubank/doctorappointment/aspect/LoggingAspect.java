package com.blubank.doctorappointment.aspect;

import com.blubank.doctorappointment.config.logger.JsonServiceLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final JsonServiceLogger jsonServiceLogger;

    @Around(value = "execution(* (@org.springframework.web.bind.annotation.RequestMapping *).*(..))")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        return jsonServiceLogger.log(pjp);
    }
}
