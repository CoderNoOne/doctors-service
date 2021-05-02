package com.app.application.proxy;

import com.app.application.dto.GetUserDto;
import com.app.infrastructure.security.dto.TokensDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PatientServiceProxy {

    private final WebClient webClient;

    public PatientServiceProxy(final WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://patient-service/patients")
                .build();
    }

    public Mono<GetUserDto> getPatientByUsername(String username) {
        return null;
    }
}
