package com.app.infrastructure.utils;

import com.app.application.exception.AppExceptionMarker;
import com.app.infrastructure.security.dto.AppError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface RoutingHandlersUtils {
    static <T> Mono<ServerResponse> toServerResponse(Mono<T> mono, HttpStatus status) {
        return mono
                .flatMap(item -> ServerResponse
                        .status(status)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(item)))
                .onErrorResume(e -> ServerResponse
                        .status(e instanceof AppExceptionMarker appEx ? appEx.getStatus() : 500)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(AppError
                                .builder()
                                .error(e.getMessage())
                                .build()))
                );
    }
}
