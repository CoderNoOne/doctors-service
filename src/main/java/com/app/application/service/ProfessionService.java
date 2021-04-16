package com.app.application.service;

import com.app.application.dto.CreateProfessionDto;
import com.app.application.dto.ProfessionDetailsDto;
import com.app.application.dto.ProfessionDto;
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

    public Mono<ProfessionDetailsDto> findByNameWithFetchedDoctors(String name) {

        return professionRepository.findByName(name)
                .map(Profession::toDetails);

    }

    public Mono<ProfessionDto> saveProfession(CreateProfessionDto createProfessionDto) {

        return professionRepository.add(createProfessionDto.toEntity())
                .map(Profession::toDto);

    }
}
