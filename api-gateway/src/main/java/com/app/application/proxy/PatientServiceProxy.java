package com.app.application.proxy;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PatientServiceProxy {

    private final WebClient webClient;

    public PatientServiceProxy(final WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://patient-service/patients")
                .build();
    }
}
