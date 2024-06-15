package com.blubank.doctorappointment.config.logger;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024
 */
@Component
@RequiredArgsConstructor
public class JsonServiceLogger extends ServiceLogger {
    private final ObjectMapper mapper;

    @Override
    public String getRequestLog(String serviceName, Object[] methodArgs, String[] parameterNames) {
        return createJson(serviceName, "request", methodArgs, parameterNames, null);
    }

    @Override
    public String getResponseLog(String serviceName, Object result, Double duration) {
        return createJson(serviceName, "response", new Object[]{result}, null, duration);
    }

    @Override
    public String getExceptionLog(String serviceName, Throwable ex, Double duration) {
        Map<String, Object> exception = new LinkedHashMap<>();
        exception.put("name", ex.getClass().getSimpleName());
        exception.put("message", ex.getMessage());
        exception.put("localizedMessage", ex.getLocalizedMessage());
        exception.put("stackTrace", getStackTrace(ex));
        return createJson(serviceName, "exception", new Object[]{exception}, null, duration);
    }

    private List<String> getStackTrace(final Throwable throwable) {
        List<String> stackTrace = new ArrayList<>();
        for (StackTraceElement element : throwable.getStackTrace()) {
            stackTrace.add(element.toString());
            if (stackTrace.size() > 14) {
                break;
            }
        }
        return stackTrace;
    }

    @SneakyThrows
    private String createJson(String serviceName, String key, Object[] objects, Object[] parameterNames, Double duration) {
        Map<String, Object> object = new LinkedHashMap<>(3);
        object.put("service", serviceName);
        if (duration != null) {
            object.put("duration", duration + "s");
        }
        if (objects != null) {
            Map<String, Object> objectsMap = new LinkedHashMap<>(objects.length);
            for (int i = 0; i < objects.length; i++) {
                Object obj = objects[i];
                if (obj != null) {
                    if (parameterNames != null && parameterNames.length > i && parameterNames[i] != null) {
                        objectsMap.put((String) parameterNames[i], obj);
                    } else {
                        objectsMap.put(obj.getClass().getSimpleName(), obj);
                    }
                }
            }
            object.put(key, objectsMap);
        }
        return mapper.writeValueAsString(object);
    }
}