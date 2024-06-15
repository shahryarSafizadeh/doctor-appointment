package com.blubank.doctorappointment.config.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024
 */
public abstract class ServiceLogger {

    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        Class joinPointLocation = pjp.getSourceLocation().getWithinType();
        String serviceName = pjp.getSignature().getName();
        Logger logger = LoggerFactory.getLogger(joinPointLocation);
        long startTime = System.currentTimeMillis();
        try {
            String[] parameterNames = ((MethodSignature) pjp.getSignature()).getParameterNames();
            logger.info(getRequestLog(serviceName, pjp.getArgs(), parameterNames));
            Object result = pjp.proceed();
            logger.info(getResponseLog(serviceName, result, (System.currentTimeMillis() - startTime) / 1000.0));
            return result;
        } catch (Throwable ex) {
            logger.info(getExceptionLog(serviceName, ex, (System.currentTimeMillis() - startTime) / 1000.0), ex);
            throw ex;
        }
    }

    public abstract String getRequestLog(String serviceName, Object[] methodArgs, String[] parameterNames);

    public abstract String getResponseLog(String serviceName, Object result, Double duration);

    public abstract String getExceptionLog(String serviceName, Throwable ex, Double duration);
}