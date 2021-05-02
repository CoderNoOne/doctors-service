package com.app.domain.patient;

import com.app.domain.generic.CrudRepository;

import java.util.concurrent.CompletionStage;

public interface PatientRepository extends CrudRepository<Patient, Long> {

    CompletionStage<Patient> findByUsername(String username);
}
