package com.app.infrastructure.routing.handlers;

import com.app.application.service.ProfessionService;
import com.app.infrastructure.utils.RoutingHandlersUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ProfessionsHandler {

    private final ProfessionService professionService;


    public Mono<ServerResponse> getAllProfessionByNames(ServerRequest serverRequest) {

        return RoutingHandlersUtils.toServerResponse(
                serverRequest
                        .bodyToFlux(String.class)
                        .collectList()
                        .map(professionService::getAllProfessionByNames)
                        .flatMapMany(Function.identity())
                        .collectList(),
                HttpStatus.OK);
    }

    public Mono<ServerResponse> getProfessionDetailsByName(ServerRequest serverRequest) {

        return RoutingHandlersUtils.toServerResponse(
                professionService.findByName(serverRequest.pathVariable("name")),
                HttpStatus.OK
        );
    }
}
