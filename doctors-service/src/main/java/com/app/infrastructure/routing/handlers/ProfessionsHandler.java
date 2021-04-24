package com.app.infrastructure.routing.handlers;

import com.app.application.dto.CreateProfessionDto;
import com.app.application.service.profession.ProfessionService;
import com.app.domain.profession.ProfessionRepository;
import com.app.infrastructure.utils.RoutingHandlersUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ProfessionsHandler {

    private final ProfessionService professionService;


    public Mono<ServerResponse> getAllProfessionByNames(ServerRequest serverRequest) {

        return RoutingHandlersUtils.toServerResponse(
                serverRequest
                        .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                        })
                        .map(professionService::getAllProfessionByNames)
                        .flatMapMany(Function.identity())
                        .collectList(),
                HttpStatus.OK);
    }

    public Mono<ServerResponse> getProfessionDetailsByName(ServerRequest serverRequest) {

        return RoutingHandlersUtils.toServerResponse(
                professionService.findByNameWithFetchedDoctors(serverRequest.pathVariable("name")),
                HttpStatus.OK
        );
    }

    public Mono<ServerResponse> createProfession(ServerRequest serverRequest) {

        return RoutingHandlersUtils.toServerResponse(
                serverRequest.bodyToMono(CreateProfessionDto.class).flatMap(professionService::saveProfession),
                HttpStatus.CREATED);

    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest) {

        return RoutingHandlersUtils.toServerResponse(
                professionService.findById(serverRequest.pathVariable("id")),
                HttpStatus.OK
        );
    }

    public Mono<ServerResponse> saveAll(ServerRequest serverRequest) {

        return RoutingHandlersUtils.toServerResponse(
                serverRequest.bodyToMono(new ParameterizedTypeReference<List<CreateProfessionDto>>() {})
                        .flatMapMany(professionService::saveAll)
                        .collectList(),
                HttpStatus.CREATED
        );
    }
}
