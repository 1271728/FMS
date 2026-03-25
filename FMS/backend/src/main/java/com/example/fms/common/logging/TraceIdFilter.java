package com.example.fms.common.logging;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter extends org.springframework.web.filter.OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String incoming = request.getHeader(TraceIdSupport.HEADER_NAME);
        String traceId = (incoming == null || incoming.trim().isEmpty())
                ? UUID.randomUUID().toString().replace("-", "")
                : incoming.trim();
        MDC.put(TraceIdSupport.MDC_KEY, traceId);
        response.setHeader(TraceIdSupport.HEADER_NAME, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TraceIdSupport.MDC_KEY);
        }
    }
}
