package com.app.infrastructure.repository.impl;

import com.app.domain.patient.Patient;
import com.app.domain.patient.PatientRepository;
import com.app.infrastructure.utils.DatabaseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Repository
@RequiredArgsConstructor
public class PatientRepositoryImpl implements PatientRepository {

    private final DatabaseUtils databaseUtils;

    @Override
    public CompletionStage<Patient> add(Patient patient) {
        return databaseUtils.saveEntity(patient);
    }

    @Override
    public CompletionStage<List<Patient>> addMany(List<Patient> items) {
        return null;
    }

    @Override
    public CompletionStage<List<Patient>> findAll() {
        return null;
    }

    @Override
    public CompletionStage<Patient> findById(Long aLong) {
        return null;
    }

    @Override
    public CompletionStage<List<Patient>> findAllById(List<Long> longs) {
        return null;
    }

    @Override
    public CompletionStage<Patient> deleteById(Long aLong) {
        return null;
    }

    @Override
    public CompletionStage<List<Patient>> deleteAllById(List<Long> longs) {
        return null;
    }
}
