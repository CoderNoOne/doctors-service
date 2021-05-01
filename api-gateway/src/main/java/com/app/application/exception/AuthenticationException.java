package com.app.application.exception;

public class AuthenticationException extends RuntimeException implements AppExceptionMarker{

    public AuthenticationException(String message) {
        super(message);
    }

    @Override
    public int getStatus() {
        return 401;
    }
}
