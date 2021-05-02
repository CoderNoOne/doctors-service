package com.app.application.service;

import com.app.application.dto.CreatePatientDto;
import com.app.application.exception.NotFoundException;
import com.app.application.exception.NotValidIdException;
import com.app.domain.patient.Patient;
import com.app.domain.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientsService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<Patient> savePatient(CreatePatientDto createPatientDto) {
        var patientToSave = createPatientDto.toEntity();
        patientToSave.setPassword(passwordEncoder.encode(createPatientDto.getPassword()));
        return Mono.fromCompletionStage(patientRepository.add(patientToSave));
    }

    public Mono<Patient> getByUsername(String username) {

        return Mono.fromCompletionStage(() -> patientRepository.findByUsername(username));
    }

    public Mono<Patient> getById(String id) {

        if (Objects.isNull(id) || !id.matches("[1-9][\\d]*")) {
            return Mono.error(() -> new NotValidIdException("Id: %s is not valid".formatted(id)));
        }

        return Mono.fromCompletionStage(patientRepository.findById(Long.parseLong(id)))
                .switchIfEmpty(Mono.error(() -> new NotFoundException("No patient with id: %s".formatted(id))));
    }
}
