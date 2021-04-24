package com.app.application.exception.handler;

import com.app.application.exception.AppExceptionMarker;
import com.app.infrastructure.routing.dto.ErrorDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
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
        serverWebExchange.getResponse().setRawStatusCode(throwable instanceof AppExceptionMarker ex ? ex.getStatus() : 500);

        return serverWebExchange
                .getResponse()
                .writeWith(Mono.just(throwable)
                        .map(this::handleExceptions));
    }

    private DataBuffer handleExceptions(Throwable throwable) {

        log.error(throwable.getMessage(), throwable);

        if (throwable instanceof AppExceptionMarker ex) {
            try {
                return dataBufferFactory
                        .wrap(objectMapper.writeValueAsBytes(ErrorDto.builder()
                                .message(ex.getMessage())
                                .build()));
            } catch (JsonProcessingException exception) {
                return dataBufferFactory.wrap(new byte[]{});
            }
        }

        return dataBufferFactory.wrap(new byte[]{});
    }
}
