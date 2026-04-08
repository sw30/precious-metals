package com.pm.preciousmetals.infrastructure.web.error;

import java.time.LocalDateTime;

public record ApiError(
        String errorCode,
        String message,
        String path,
        String traceId,
        LocalDateTime timestamp
) {}
