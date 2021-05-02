package com.app.application.exception;

import com.app.infrastructure.security.dto.AppError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@Order(-2)
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;
    private final DataBufferFactory dataBufferFactory;


    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {

        serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        serverWebExchange.getResponse().setRawStatusCode(throwable instanceof AppExceptionMarker marker ? marker.getStatus() : 500);

        return serverWebExchange
                .getResponse()
                .writeWith(Mono.just(throwable)
                        .map(this::handleExceptions));
    }

    private DataBuffer handleExceptions(Throwable throwable) {

        log.error(throwable.getMessage(), throwable);

        try {
            return dataBufferFactory
                    .wrap(objectMapper.writeValueAsBytes(AppError.builder()
                            .error(throwable.getMessage())
                            .build()));
        } catch (JsonProcessingException exception) {
            return dataBufferFactory.wrap(new byte[]{});
        }

    }
}

