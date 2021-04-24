package com.app.application.exception;

public class ProfessionAlreadyExistsException extends RuntimeException implements AppExceptionMarker{

    @Override
    public int getStatus() {
        return 400;
    }

    public ProfessionAlreadyExistsException(String message) {
        super(message);
    }
}
