package com.app.application.proxy;

import com.app.application.dto.GetDoctorDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DoctorServiceProxy {

    private final WebClient webClient;

    public DoctorServiceProxy(final WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://doctors-service/doctors")
                .build();
    }

    public Mono<GetDoctorDto> getDoctorById(final Long id) {
        return webClient
                .get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(GetDoctorDto.class);
    }
}
