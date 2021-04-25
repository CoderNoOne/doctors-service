package com.app.infrastructure.routing;

import com.app.infrastructure.routing.handlers.DoctorsHandler;
import com.app.infrastructure.routing.handlers.ProfessionsHandler;
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
    public RouterFunction<ServerResponse> doctorsRoute(DoctorsHandler doctorsHandler) {
        return route(POST("/register").and(accept(MediaType.APPLICATION_JSON)), doctorsHandler::addDoctor)
                .andRoute(GET("/doctors/{id}").and(accept(MediaType.APPLICATION_JSON)), doctorsHandler::getDoctorById)
                .andRoute(GET("/doctors/all/byIds").and(accept(MediaType.APPLICATION_JSON)), doctorsHandler::getAllByIds)
                .andRoute(GET("/doctors/{id}/professions").and(accept(MediaType.APPLICATION_JSON)), doctorsHandler::getProfessionsByDoctorId)
                .andRoute(POST("/doctors/addMultiple").and(accept(MediaType.APPLICATION_JSON)), doctorsHandler::addDoctors)
                .andRoute(PUT("/doctors/id/{doctorId}/professions/add").and(accept(MediaType.APPLICATION_JSON)), doctorsHandler::addProfessionForDoctor)
                .andRoute(GET("/doctors").and(accept(MediaType.APPLICATION_JSON)), doctorsHandler::getAllDoctors)
                .andRoute(GET("/doctors/username/{username}").and(accept(MediaType.APPLICATION_JSON)), doctorsHandler::getDoctorByUsername);


    }

    @Bean
    public RouterFunction<ServerResponse> professionsRoute(ProfessionsHandler professionsHandler) {
        return route(GET("/professions/filter/names").and(accept(MediaType.APPLICATION_JSON)), professionsHandler::getAllProfessionByNames)
                .andRoute(GET("/professions/name/{name}").and(accept(MediaType.APPLICATION_JSON)), professionsHandler::getProfessionDetailsByName)
                .andRoute(GET("/professions/id/{id}").and(accept(MediaType.APPLICATION_JSON)), professionsHandler::findById)
                .andRoute(POST("/professions").and(accept(MediaType.APPLICATION_JSON)), professionsHandler::createProfession)
                .andRoute(POST("/professions/saveMultiple").and(accept(MediaType.APPLICATION_JSON)), professionsHandler::saveAll);

    }


}
