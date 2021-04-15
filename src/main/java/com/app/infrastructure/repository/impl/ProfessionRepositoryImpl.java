package com.app.infrastructure.repository.impl;

import com.app.application.dto.SearchByFieldValueDto;
import com.app.application.dto.SearchByFieldValuesDto;
import com.app.domain.generic.AbstractCrudRepository;
import com.app.domain.profession.Profession;
import com.app.domain.profession.ProfessionRepository;
import com.app.infrastructure.enums.ProfessionFieldsToFetch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProfessionRepositoryImpl extends AbstractCrudRepository<Profession, Long> implements ProfessionRepository {

    private static final String PROFESSION_NAME = "name";

    @Override
    public Flux<Profession> findAllByNames(List<String> names) {

        return Mono.fromCompletionStage(
                databaseUtils
                        .findByFieldValues(SearchByFieldValuesDto.<String>builder()
                                        .fieldName(PROFESSION_NAME)
                                        .fieldValues(names)
                                        .build(),
                                Profession.class,
                                ProfessionFieldsToFetch.DOCTORS.getFieldName()))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Profession> findByName(String name) {

        return Mono.fromCompletionStage(databaseUtils.findOneByFieldValue(
                SearchByFieldValueDto.<String>builder()
                        .fieldName(PROFESSION_NAME)
                        .fieldValue(name)
                        .build(),
                Profession.class,
                ProfessionFieldsToFetch.DOCTORS.getFieldName()
        ));
    }
}
