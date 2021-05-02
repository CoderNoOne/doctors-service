package com.app.infrastructure.routing.handlers;

import com.app.application.dto.type.Role;
import com.app.application.exception.AuthenticationException;
import com.app.infrastructure.security.service.LoginService;
import com.app.infrastructure.security.dto.AuthenticationDto;
import com.app.infrastructure.security.tokens.AppTokensService;
import com.app.infrastructure.utils.RoutingHandlersUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class SecurityHandler {

    private final LoginService loginService;
    private final PasswordEncoder passwordEncoder;
    private final AppTokensService appTokensService;

    public Mono<ServerResponse> login(ServerRequest serverRequest) {

        var authenticationDtoMono = serverRequest.bodyToMono(AuthenticationDto.class);

        return RoutingHandlersUtils.toServerResponse(authenticationDtoMono
                        .switchIfEmpty(Mono.error(() -> new AuthenticationException("Provide request body")))
                        .map(dto -> {
                            if (isNull(dto.getPassword()) || isNull(dto.getUsername())) {
                                throw new AuthenticationException("Provide password and username");
                            }
                            if (isNull(dto.getRole()) || Arrays.stream(Role.values()).noneMatch(enumVal -> enumVal.toString().equalsIgnoreCase(dto.getRole()))) {
                                throw new AuthenticationException("Role not valid. Must be one of: %s".formatted(Arrays.toString(Role.values())));
                            }
                            return dto;
                        })
                        .flatMap(authenticationDto -> loginService
                                .findByUsername(authenticationDto.getUsername(), Role.valueOf(authenticationDto.getRole()))
                                .filter(user -> passwordEncoder.matches(authenticationDto.getPassword(), user.getPassword())))
                        .switchIfEmpty(Mono.error(() -> new AuthenticationException("Provide valid credentials")))
                        .flatMap(user -> appTokensService.generateTokens(user, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).findAny().orElse(null))),
                HttpStatus.CREATED);


    }

    public Mono<ServerResponse> getUser(ServerRequest serverRequest) {

        return RoutingHandlersUtils.toServerResponse(
                loginService.findByUsername(serverRequest.pathVariable("username"), Role.ROLE_DOCTOR),
                HttpStatus.OK);
    }
}

