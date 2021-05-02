package com.app.infrastructure.routing;


import com.app.infrastructure.routing.handlers.PatientsHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class AppRouting {

    private final PatientsHandler patientsHandler;

    @Bean
    public RouterFunction<ServerResponse> patientsRoute(PatientsHandler patientsHandler) {
        return route(POST("patients/register").and(accept(MediaType.APPLICATION_JSON)), patientsHandler::addPatient)
                .andRoute(GET("patients/username/{username}").and(accept(MediaType.APPLICATION_JSON)), patientsHandler::getPatientByUsername)
                .andRoute(GET("/patients/id/{id}").and(accept(MediaType.APPLICATION_JSON)), patientsHandler::getPatientById);

    }




}
