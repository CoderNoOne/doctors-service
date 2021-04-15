package com.app.infrastructure.repository.impl;

import com.app.domain.doctor.Doctor;
import com.app.domain.doctor.DoctorRepository;
import com.app.domain.generic.AbstractCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class DoctorRepositoryImpl extends AbstractCrudRepository<Doctor, Long> implements DoctorRepository {


    @Override
    public Flux<Doctor> findAll() {
        return null;
    }

    @Override
    public Flux<Doctor> findAllById(List<Long> longs) {
        return null;
    }

    @Override
    public Mono<Doctor> deleteById(Long aLong) {
        return null;
    }

    @Override
    public Flux<Doctor> deleteAllById(List<Long> longs) {
        return null;
    }
}
