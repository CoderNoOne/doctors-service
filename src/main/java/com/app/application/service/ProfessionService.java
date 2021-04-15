package com.app.application.service;

import com.app.application.dto.ProfessionDetailsDto;
import com.app.application.exception.NotFoundException;
import com.app.domain.profession.Profession;
import com.app.domain.profession.ProfessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessionService {

    private final ProfessionRepository professionRepository;

    public Flux<ProfessionDetailsDto> getAllProfessionByNames(List<String> names) {

        return professionRepository.findAllByNames(names)
                .map(Profession::toDetails);

    }

    public Mono<ProfessionDetailsDto> findByName(String name) {

        return professionRepository.findByName(name)
                .map(Profession::toDetails)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("No profession with name: %s".formatted(name))));

    }
}
