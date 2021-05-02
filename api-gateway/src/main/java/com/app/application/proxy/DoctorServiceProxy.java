package com.app.application.proxy;


import com.app.application.dto.GetDoctorDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class DoctorServiceProxy {

    private final WebClient webClient;

    @Value("${spring.profiles.active}")
    private String profile;

    public DoctorServiceProxy(final WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://%s:8100/doctors".formatted("local".equalsIgnoreCase(profile) ? "localhost": "doctors-service"))
                .build();
    }

    public Mono<GetDoctorDto> getDoctorByUsername(final String username) {
        return webClient
                .get()
                .uri("/username/" + username)
                .retrieve()
                .bodyToMono(GetDoctorDto.class);
    }

    public Mono<GetDoctorDto> getDoctorById(final Long id) {
        return webClient
                .get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(GetDoctorDto.class);
    }
}
