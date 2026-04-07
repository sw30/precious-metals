package com.pm.preciousmetals.infrastructure.adapter.web.error;

import java.time.LocalDateTime;

public record ApiError(
        String errorCode,
        String message,
        String path,
        String traceId,
        LocalDateTime timestamp
) {}
