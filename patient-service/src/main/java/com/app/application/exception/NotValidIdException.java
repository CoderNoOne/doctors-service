package com.app.application.exception;

public class NotValidIdException extends RuntimeException implements AppExceptionMarker {

    public NotValidIdException(String message) {
        super(message);
    }

    @Override
    public int getStatus() {
        return 400;
    }
}
