package com.app.application.exception;

public class ProfessionAlreadyHasTheProfessionException extends RuntimeException implements AppExceptionMarker{

    public ProfessionAlreadyHasTheProfessionException(String message) {
        super(message);
    }

    @Override
    public int getStatus() {
        return 400;
    }
}
