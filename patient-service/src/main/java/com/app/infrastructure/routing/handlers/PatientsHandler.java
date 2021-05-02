package com.app.infrastructure.routing.handlers;

import com.app.application.dto.CreatePatientDto;
import com.app.application.service.PatientsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PatientsHandler {

    private final PatientsService patientsService;

    public Mono<ServerResponse> addPatient(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(CreatePatientDto.class)
                .flatMap(patientsService::savePatient)
                .flatMap(savedDoctor -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(savedDoctor))
                );
    }
}
