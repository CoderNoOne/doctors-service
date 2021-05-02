package com.app.infrastructure.security;

import com.app.application.exception.AuthenticationException;
import com.app.application.proxy.DoctorServiceProxy;
import com.app.infrastructure.security.tokens.AppTokensService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final AppTokensService appTokensService;
    private final DoctorServiceProxy proxy;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {


        System.out.println();
//        log.info("Authentication credentials: " + authentication.getCredentials());
        var role = appTokensService.getRole(authentication.getCredentials().toString());
        log.info("Authentication role: " + role);


        try {
            if (!appTokensService.isTokenValid(authentication.getCredentials().toString())) {
                return Mono.error(() -> new AuthenticationException("AUTH FAILED - TOKEN IS NOT VALID"));
            }
            return proxy
                    .getDoctorById(Long.parseLong(appTokensService.getId(authentication.getCredentials().toString())))
                    .switchIfEmpty(Mono.error(() -> new AuthenticationException("Wrong username")))
                    .map(userFromDb -> new UsernamePasswordAuthenticationToken(
                            userFromDb.getUsername(),
                            null,
                            Collections.emptyList()
                           /* List.of(new SimpleGrantedAuthority(userFromDb.getRole().toString())*/)
                    );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Mono.error(() -> new AuthenticationException("User cannot be authenticated"));
        }
    }
}
