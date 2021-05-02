package com.app.infrastructure.security;

import com.app.application.dto.type.Role;
import com.app.application.exception.AuthenticationException;
import com.app.application.proxy.DoctorServiceProxy;
import com.app.application.proxy.PatientServiceProxy;
import com.app.infrastructure.security.tokens.AppTokensService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final AppTokensService appTokensService;
    private final DoctorServiceProxy doctorServiceProxy;
    private final PatientServiceProxy patientServiceProxy;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        try {
            if (!appTokensService.isTokenValid(authentication.getCredentials().toString())) {
                return Mono.error(() -> new AuthenticationException("AUTH FAILED - TOKEN IS NOT VALID"));
            }

            var role = appTokensService.getRole(authentication.getCredentials().toString());

            if (Arrays.stream(Role.values()).noneMatch(enumVal -> enumVal.name().equals(role))) {
                return Mono.error(() -> new AuthenticationException("AUTH FAILED - NOT VALID ROLE"));
            }

            var userId = Long.parseLong(appTokensService.getId(authentication.getCredentials().toString()));

            return (switch (Role.valueOf(role)) {
                case ROLE_DOCTOR -> doctorServiceProxy
                        .getDoctorById(userId);
                case ROLE_PATIENT -> patientServiceProxy
                        .getPatientById(userId);
            })
                    .switchIfEmpty(Mono.error(() -> new AuthenticationException("Wrong username")))
                    .map(userFromDb -> new UsernamePasswordAuthenticationToken(
                            userFromDb.getUsername(),
                            null,
                            List.of(new SimpleGrantedAuthority(role)
                            )));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Mono.error(() -> new AuthenticationException("User cannot be authenticated"));
        }
    }
}
