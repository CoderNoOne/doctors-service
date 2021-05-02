package com.app.infrastructure.security.service;

import com.app.application.dto.type.Role;
import com.app.application.proxy.DoctorServiceProxy;
import com.app.application.proxy.PatientServiceProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {

    protected final DoctorServiceProxy doctorServiceProxy;
    private final PatientServiceProxy patientServiceProxy;

    public Mono<User> findByUsername(String username, Role role) {

        var isDoctor = Role.ROLE_DOCTOR.equals(role);

        return (isDoctor ? doctorServiceProxy
                .getDoctorByUsername(username)
                : patientServiceProxy.getPatientByUsername(username))
                .map(getUserDto -> new User(
                        getUserDto.getUsername(),
                        getUserDto.getPassword(),
                        true, true, true, true,
                        List.of(new SimpleGrantedAuthority(isDoctor ? "ROLE_DOCTOR" : "ROLE_PATIENT"))
                ));
    }
}
