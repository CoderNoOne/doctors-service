package com.app.infrastructure.routing;

import com.app.infrastructure.routing.handlers.SecurityHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AppRouting {

    @Bean
    public RouterFunction<ServerResponse> doctorsRoute(SecurityHandler securityHandler) {
        return route(POST("security/login").and(accept(MediaType.APPLICATION_JSON)), securityHandler::login);
    }
}
