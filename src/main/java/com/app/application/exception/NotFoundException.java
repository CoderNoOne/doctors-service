package com.app.application.exception;

public class NotFoundException extends RuntimeException implements AppExceptionMarker{

    private static final int STATUS = 404;

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public int getStatus() {
        return STATUS;
    }
}
