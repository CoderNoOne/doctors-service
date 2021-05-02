package com.app.infrastructure.routing.handlers;

import com.app.application.dto.CreatePatientDto;
import com.app.application.service.PatientsService;
import com.app.infrastructure.utils.RoutingHandlersUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PatientsHandler {

    private final PatientsService patientsService;

    public Mono<ServerResponse> addPatient(ServerRequest serverRequest) {

        return RoutingHandlersUtils.toServerResponse(serverRequest.bodyToMono(CreatePatientDto.class)
                        .flatMap(patientsService::savePatient),
                HttpStatus.CREATED);
    }

    public Mono<ServerResponse> getPatientByUsername(ServerRequest serverRequest) {

        return RoutingHandlersUtils
                .toServerResponse(
                        patientsService.getByUsername(serverRequest.pathVariable("username")),
                        HttpStatus.OK
                );

    }


}
