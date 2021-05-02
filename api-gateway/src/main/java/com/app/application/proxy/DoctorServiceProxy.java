package com.app.application.proxy;


import com.app.application.dto.GetUserDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    public Mono<GetUserDto> getDoctorByUsername(final String username) {
        return webClient
                .get()
                .uri("/username/" + username)
                .retrieve()
                .bodyToMono(GetUserDto.class);
    }

    public Mono<GetUserDto> getDoctorById(final Long id) {
        return webClient
                .get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(GetUserDto.class);
    }
}
