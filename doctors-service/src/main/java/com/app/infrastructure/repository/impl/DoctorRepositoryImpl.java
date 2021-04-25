package com.app.infrastructure.repository.impl;

import com.app.application.dto.SearchByFieldValueDto;
import com.app.application.dto.SearchByFieldValuesDto;
import com.app.domain.doctor.Doctor;
import com.app.domain.doctor.DoctorRepository;
import com.app.infrastructure.enums.DoctorsFieldsToFetch;
import com.app.infrastructure.utils.DatabaseUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;


@Repository
@RequiredArgsConstructor
public class DoctorRepositoryImpl implements DoctorRepository {

    private final DatabaseUtils databaseUtils;
    private final Stage.SessionFactory sessionFactory;

    @Override
    public CompletionStage<Doctor> add(Doctor doctor) {
        return databaseUtils.saveEntity(doctor);
    }

    @Override
    public CompletionStage<List<Doctor>> addMany(List<Doctor> doctors) {
        return databaseUtils.saveEntities(doctors);
    }

    @Override
    public CompletionStage<List<Doctor>> findAll() {
        return databaseUtils.findAll(Doctor.class, DoctorsFieldsToFetch.PROFESSIONS.getFieldName());
    }

    @Override
    public CompletionStage<Doctor> findById(Long id) {

        return databaseUtils
                .findOneByFieldValue(SearchByFieldValueDto.<Long>builder()
                                .fieldName("id")
                                .fieldValue(id)
                                .build(),
                        Doctor.class,
                        DoctorsFieldsToFetch.PROFESSIONS.getFieldName(),
                        Function.identity()
                );

    }

    @Override
    public CompletionStage<List<Doctor>> findAllById(List<Long> ids) {

        return databaseUtils
                .findAllByFieldValues(SearchByFieldValuesDto.<Long>builder()
                                .fieldName("id")
                                .fieldValues(ids)
                                .build(),
                        Doctor.class,
                        DoctorsFieldsToFetch.PROFESSIONS.getFieldName()
                );
    }

    @Override
    public CompletionStage<Doctor> findByUsername(String username) {
        return databaseUtils
                .findOneByFieldValue(SearchByFieldValueDto.<String>builder()
                                .fieldName("username")
                                .fieldValue(username)
                                .build(),
                        Doctor.class,
                        DoctorsFieldsToFetch.PROFESSIONS.getFieldName(),
                        Function.identity()
                );
    }

    @Override
    public CompletionStage<Doctor> deleteById(Long aLong) {
        return null;
    }

    @Override
    public CompletionStage<List<Doctor>> deleteAllById(List<Long> longs) {
        return null;
    }
}
