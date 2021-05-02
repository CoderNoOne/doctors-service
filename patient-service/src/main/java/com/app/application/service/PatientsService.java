package com.app.application.service;

import com.app.application.dto.CreatePatientDto;
import com.app.domain.patient.Patient;
import com.app.domain.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientsService {

    private final PatientRepository patientRepository;

    public Mono<Patient> savePatient(CreatePatientDto createPatientDto) {
        var item = createPatientDto.toEntity();
        return Mono.fromCompletionStage(patientRepository.add(item));
    }

    public Mono<Patient> getByUsername(String username) {

        return Mono.fromCompletionStage(() -> patientRepository.findByUsername(username));
    }

}