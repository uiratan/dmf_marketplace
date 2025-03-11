package com.dmf.marketplace.compartilhado.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, List<String>> errors;
    private String path;

    // Construtor
    public ErrorResponse(int status, String error, String message, LocalDateTime timestamp, Map<String, List<String>> errors, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
        this.errors = errors;
        this.path = path;
    }

    // Getters e Setters
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Map<String, List<String>> getErrors() { return errors; }
    public String getPath() { return path; }
}