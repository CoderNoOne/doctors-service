package com.app.infrastructure.security.service;

import com.app.application.proxy.DoctorServiceProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorLoginService {

    protected final DoctorServiceProxy proxy;

    public Mono<User> findByUsername(String username) {
        return proxy
                .getDoctorByUsername(username)
                .map(getDoctorDto -> new User(
                        getDoctorDto.getUsername(),
                        getDoctorDto.getPassword(),
                        true, true, true, true,
                        List.of(new SimpleGrantedAuthority(getDoctorDto.getRole().toString()))
                ));
    }
}