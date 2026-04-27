package com.hcmunre.library.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
    private Map<String, String> details;

    public static Builder builder() {
        return new Builder();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public static class Builder {
        private final ErrorResponse value = new ErrorResponse();

        public Builder timestamp(LocalDateTime timestamp) {
            value.timestamp = timestamp;
            return this;
        }

        public Builder status(int status) {
            value.status = status;
            return this;
        }

        public Builder error(String error) {
            value.error = error;
            return this;
        }

        public Builder code(String code) {
            value.code = code;
            return this;
        }

        public Builder message(String message) {
            value.message = message;
            return this;
        }

        public Builder path(String path) {
            value.path = path;
            return this;
        }

        public Builder details(Map<String, String> details) {
            value.details = details;
            return this;
        }

        public ErrorResponse build() {
            return value;
        }
    }
}
