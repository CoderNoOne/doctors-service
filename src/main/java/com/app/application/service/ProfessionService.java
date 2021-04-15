package com.app.application.service;

import com.app.application.dto.ProfessionDetailsDto;
import com.app.application.dto.SearchByFieldValueDto;
import com.app.application.exception.NotFoundException;
import com.app.domain.profession.Profession;
import com.app.domain.profession.ProfessionRepository;
import com.app.infrastructure.enums.ProfessionFieldsToFetch;
import com.app.infrastructure.routing.dto.ErrorDto;
import com.app.infrastructure.utils.DatabaseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProfessionService {

    private final ProfessionRepository professionRepository;
    private final DatabaseUtils databaseUtils;

    public Flux<ProfessionDetailsDto> getAllProfessionByNames(List<String> names) {

        return professionRepository.findAllByNames(names)
                .map(Profession::toDetails);

    }

    public Mono<ProfessionDetailsDto> findByName(String name) {

        return Mono.fromCompletionStage(databaseUtils
                .findOneByFieldValue(SearchByFieldValueDto.builder()
                                .name("name")
                                .value(name)
                                .build(),
                        Profession.class,
                        ProfessionFieldsToFetch.DOCTORS.getFieldName(),
                        profession -> Objects.nonNull(profession) ? profession.toDetails() : null
                )).switchIfEmpty(Mono.error(() -> new NotFoundException("No profession with name: %s".formatted(name))));

    }
}
