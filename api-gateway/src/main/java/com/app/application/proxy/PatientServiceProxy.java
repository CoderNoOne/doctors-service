package com.app.application.proxy;

import com.app.application.dto.GetUserDto;
import com.app.infrastructure.security.dto.TokensDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PatientServiceProxy {

    private final WebClient webClient;

    @Value("${spring.profiles.active}")
    private String profile;

    public PatientServiceProxy(final WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://%s:9000/patients".formatted("local".equalsIgnoreCase(profile) ? "localhost" : "patients-service")).build();
    }

    public Mono<GetUserDto> getPatientByUsername(String username) {
        return webClient
                .get()
                .uri("/username/" + username)
                .retrieve()
                .bodyToMono(GetUserDto.class);
    }

    public Mono<GetUserDto> getPatientById(long userId) {
        return webClient
                .get()
                .uri("id/" + userId)
                .retrieve()
                .bodyToMono(GetUserDto.class);
    }
}
