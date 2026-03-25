package com.example.fms.common.logging;

import org.slf4j.MDC;

public final class TraceIdSupport {

    public static final String MDC_KEY = "traceId";
    public static final String HEADER_NAME = "X-Trace-Id";

    private TraceIdSupport() {}

    public static String currentTraceId() {
        return MDC.get(MDC_KEY);
    }
}
