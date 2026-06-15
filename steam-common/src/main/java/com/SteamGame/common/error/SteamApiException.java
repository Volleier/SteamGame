package com.SteamGame.common.error;

import com.SteamGame.common.response.ApiResponse;

/**
 * Exception thrown when Steam API calls fail (timeout, unavailable, rate-limit, etc.).
 * Controllers should catch this and convert to ApiResponse with the appropriate ErrorCode.
 */
public class SteamApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public SteamApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SteamApiException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() { return errorCode; }

    public ApiResponse<?> toResponse() {
        return ApiResponse.fail(errorCode.getCode(), getMessage());
    }
}
