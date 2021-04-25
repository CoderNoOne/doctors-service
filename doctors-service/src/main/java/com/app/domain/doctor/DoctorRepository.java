package com.app.domain.doctor;

import com.app.domain.generic.CrudRepository;

import java.util.concurrent.CompletionStage;

public interface DoctorRepository extends CrudRepository<Doctor, Long> {

    CompletionStage<Doctor> findByUsername(String username);

}
