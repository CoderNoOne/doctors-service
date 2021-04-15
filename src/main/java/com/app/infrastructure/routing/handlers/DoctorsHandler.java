package com.app.infrastructure.routing.handlers;

import com.app.application.dto.CreateDoctorDto;
import com.app.application.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class DoctorsHandler {

    private final DoctorService doctorService;

    public Mono<ServerResponse> addDoctor(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(CreateDoctorDto.class)
                .flatMap(doctorService::saveDoctor)
                .flatMap(savedDoctor -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(savedDoctor))
                );
    }

    public Mono<ServerResponse> getDoctorById(ServerRequest serverRequest) {
        final Long id = Long.parseLong(serverRequest.pathVariable("id"));
        return doctorService.getDoctorByIdWithFetchedProfessions(id)
                .flatMap(doctor -> ServerResponse
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(doctor))
                );
    }

    public Mono<ServerResponse> getProfessionsByDoctorId(ServerRequest serverRequest) {

        return doctorService.getDoctorProfessionsByDoctorId(Long.parseLong(serverRequest.pathVariable("id")))
                .collectList()
                .flatMap(list -> ServerResponse
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(list))
                );
    }

    public Mono<ServerResponse> addDoctors(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(new ParameterizedTypeReference<List<CreateDoctorDto>>() {
        })
                .map(doctorService::saveDoctors)
                .flatMapMany(Function.identity())
                .collectList()
                .flatMap(list -> ServerResponse
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(list))
                );
    }
}
