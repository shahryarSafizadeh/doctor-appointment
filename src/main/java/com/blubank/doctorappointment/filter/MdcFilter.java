package com.blubank.doctorappointment.filter;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Shahryar Safizadeh
 * @since 6/14/2024
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcFilter extends OncePerRequestFilter {
    private static final String MDC_CLIENT_IP = "clientIp";
    private static final String HEADER_REQUEST_ID = "X-Request-ID";
    private static final String MDC_REQUEST_ID = "requestId";
    private final static String REQUEST_ID_PREFIX = "BLU-";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String xRequestId = request.getHeader(HEADER_REQUEST_ID);
        if (StringUtils.isBlank(xRequestId)) {
            xRequestId = REQUEST_ID_PREFIX + RandomStringUtils.randomAlphanumeric(8);
        }
        MDC.put(MDC_REQUEST_ID, xRequestId);

        String clientIp = request.getRemoteAddr();
        MDC.put(MDC_CLIENT_IP, clientIp);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
