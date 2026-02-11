package com.aincrad.know_recipes_be.exception;

import java.time.Instant;

public record ApiErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path
) {}
