package com.app.application.service.profession;

import com.app.application.dto.CreateProfessionDto;
import com.app.application.dto.ProfessionDetailsDto;
import com.app.application.dto.ProfessionDto;
import com.app.application.exception.NotFoundException;
import com.app.application.exception.NotValidIdException;
import com.app.application.exception.ProfessionAlreadyExistsException;
import com.app.domain.profession.Profession;
import com.app.domain.profession.ProfessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfessionService {

    private final ProfessionRepository professionRepository;

    public Flux<ProfessionDetailsDto> getAllProfessionByNames(List<String> names) {

        return Mono.fromCompletionStage(professionRepository.findAllByNames(names))
                .flatMapMany(Flux::fromIterable)
                .map(Profession::toDetails);

    }

    public Mono<ProfessionDetailsDto> findByNameWithFetchedDoctors(String name) {

        return Mono.fromCompletionStage(professionRepository.findByName(name))
                .map(Profession::toDetails);

    }

    public Mono<ProfessionDto> saveProfession(CreateProfessionDto createProfessionDto) {

        return Mono.fromCompletionStage(professionRepository.doExistsByName(createProfessionDto.getName())
                .thenCompose(exists -> {
                    if (!exists) {
                        return professionRepository.add(createProfessionDto.toEntity());
                    }
                    throw new ProfessionAlreadyExistsException("There is already a profession with name: %s".formatted(createProfessionDto.getName()));
                }))
                .map(Profession::toDto);

    }

    public Mono<ProfessionDetailsDto> findById(String id) {

        if (Objects.isNull(id) || !id.matches("[1-9][\\d]*")) {
            return Mono.error(() -> new NotValidIdException("Id: %s is not valid".formatted(id)));
        }

        return Mono.fromCompletionStage(professionRepository.findById(Long.parseLong(id)))
                .map(Profession::toDetails)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("No profession with id: %s".formatted(id))));
    }

    public Flux<ProfessionDto> saveAll(List<CreateProfessionDto> professionsToSave) {

        var professionsNamesToSave = professionsToSave.stream().map(CreateProfessionDto::getName).collect(Collectors.toList());

        return Mono.fromCompletionStage(professionRepository.findAllByNames(professionsNamesToSave)
                .thenApply(professionsFromDB -> {
                    var professionsNames = professionsFromDB.stream().map(Profession::getName).collect(Collectors.toList());
                    professionsToSave.removeIf(prToSave -> professionsNames.contains(prToSave.getName()));
                    log.warn("Professions %s skipped. Already in DB".formatted(professionsNames));

                    return professionsToSave;
                })
                .thenCompose(updatedProfessionsToSave -> professionRepository.addMany(updatedProfessionsToSave.stream().distinct().map(CreateProfessionDto::toEntity).collect(Collectors.toList()))))
                .flatMapMany(Flux::fromIterable)
                .map(Profession::toDto);

    }

}
