package com.app.application.service.doctor;

import com.app.application.dto.CreateDoctorDto;
import com.app.application.dto.CreateProfessionDto;
import com.app.application.dto.DoctorDetails;
import com.app.application.dto.ProfessionDto;
import com.app.application.exception.NotFoundException;
import com.app.application.exception.NotValidIdException;
import com.app.application.exception.ProfessionAlreadyHasTheProfessionException;
import com.app.domain.doctor.Doctor;
import com.app.domain.doctor.DoctorRepository;
import com.app.domain.profession.Profession;
import com.app.domain.profession.ProfessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ProfessionRepository professionRepository;

    public Mono<DoctorDetails> saveDoctor(final CreateDoctorDto createDoctorDto) {

        final Doctor doctorToSave = createDoctorDto.toEntity();
        final List<String> professionsNames = Objects.nonNull(createDoctorDto.getProfessions()) ? createDoctorDto.getProfessions().stream().map(ProfessionDto::getName).collect(Collectors.toList()) : new ArrayList<>();

        final CompletionStage<List<Profession>> professionsFromDB = professionRepository.findAllByNames(professionsNames);

        return Mono.fromCompletionStage(professionsFromDB
                .thenCompose(professions -> {
                    professionsNames.removeIf(pn -> professions.stream().anyMatch(prDB -> prDB.getName().equals(pn)));
                    final List<Profession> professionsToSave = professionsNames.stream().map(name -> Profession.builder().name(name).build()).collect(Collectors.toList());
                    return professionRepository.addMany(professionsToSave)
                            .thenApply(saved -> {
                                professions.addAll(saved);
                                doctorToSave.setProfessions(professions.stream().distinct().collect(Collectors.toList()));
                                return doctorToSave;
                            })
                            .thenCompose(doctorRepository::add);

                }))
                .map(Doctor::toDetails);
    }

    public Mono<DoctorDetails> getDoctorByIdWithFetchedProfessions(final String id) {

        if (Objects.isNull(id) || !id.matches("[1-9][\\d]*")) {
            return Mono.error(() -> new NotValidIdException("Id: %s is not valid".formatted(id)));
        }

        return Mono.fromCompletionStage(doctorRepository.findById(Long.parseLong(id)))
                .map(Doctor::toDetails)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("No doctor with id: %s".formatted(id))));
    }

    public Flux<ProfessionDto> getDoctorProfessionsByDoctorId(final String id) {

        if (Objects.isNull(id) || !id.matches("[1-9][\\d]*")) {
            return Flux.error(() -> new NotValidIdException("Id: %s is not valid".formatted(id)));
        }

        return Mono.fromCompletionStage(professionRepository.findAllByDoctorId(Long.parseLong(id)))
                .flatMapMany(Flux::fromIterable)
                .map(Profession::toDto);

    }

    public Flux<DoctorDetails> saveDoctors(final List<CreateDoctorDto> createDoctorDtoList) {

        final Map<Doctor, List<Profession>> professionsGroupedByDoctor = createDoctorDtoList
                .stream()
                .collect(Collectors.groupingBy(
                        CreateDoctorDto::toEntity,
                        Collectors.flatMapping(dto -> Objects.isNull(dto.getProfessions()) ? Stream.empty() : dto.getProfessions().stream().map(ProfessionDto::toEntity), Collectors.toList()))
                );

        final List<String> allProfessionsName = professionsGroupedByDoctor.values()
                .stream()
                .<Profession>mapMulti(Iterable::forEach)
                .map(Profession::getName)
                .collect(Collectors.toList());

        final CompletionStage<List<Profession>> professionsFromDB = professionRepository.findAllByNames(allProfessionsName);

        return Mono.fromCompletionStage(professionsFromDB
                .thenCompose(professions -> {
                    allProfessionsName.removeIf(pn -> professions.stream().anyMatch(prDB -> prDB.getName().equals(pn)));
                    final List<Profession> professionsToSave = allProfessionsName.stream().map(name -> Profession.builder().name(name).build()).distinct().collect(Collectors.toList());
                    final List<Profession> updatedProfessions = new ArrayList<>();

                    return professionRepository.addMany(professionsToSave)
                            .thenApply(nth -> {
                                updatedProfessions.addAll(professionsToSave);
                                updatedProfessions.addAll(professions);
                                return updatedProfessions;
                            })
                            .thenApply(allProfessionsFromDB ->
                                    professionsGroupedByDoctor.entrySet()
                                            .stream()
                                            .map(e -> Map.entry(e.getKey(), allProfessionsFromDB.stream().filter(profFromDb -> e.getValue().stream().anyMatch(pr -> pr.getName().equals(profFromDb.getName()))).collect(Collectors.toList())))
                                            .map(e -> {
                                                final Doctor doctor = e.getKey();
                                                doctor.setProfessions(e.getValue());
                                                return doctor;
                                            })
                                            .collect(Collectors.toList()))
                            .thenCompose(doctorRepository::addMany);
                }))
                .flatMapMany(Flux::fromIterable)
                .map(Doctor::toDetails);
    }

    public Mono<DoctorDetails> addProfessionForDoctor(final CreateProfessionDto createProfessionDto,
                                                      final String doctorId) {

        if (Objects.isNull(doctorId) || !doctorId.matches("[1-9][\\d]*")) {
            return Mono.error(() -> new NotValidIdException("Id: %s is not valid".formatted(doctorId)));
        }

        return Mono.fromCompletionStage(doctorRepository.findById(Long.parseLong(doctorId))
                .thenApply(doctorFromDB -> {
                    if (Objects.nonNull(doctorFromDB)) {
                        return doctorFromDB;
                    }
                    throw new NotFoundException("No doctor with id: %s".formatted(doctorId));
                })
                .thenApply(doctor -> {
                    if (doctor.getProfessions().stream().anyMatch(profession -> profession.getName().equals(createProfessionDto.getName()))) {
                        throw new ProfessionAlreadyHasTheProfessionException("Doctor already has the profession: %s".formatted(createProfessionDto.getName()));
                    }
                    return doctor;
                })
                .thenCompose(doctorFromDB ->
                        professionRepository.findByName(createProfessionDto.getName())
                                .handle((professionFromDB, ex) -> {
                                    if (Objects.nonNull(professionFromDB)) {
                                        doctorFromDB.getProfessions().add(professionFromDB);
                                    }
                                    if (Objects.nonNull(ex)) {
                                        log.warn(ex.getMessage());
                                        log.info("Profession with name %s  has been added".formatted(createProfessionDto.getName()));
                                    }
                                    return doctorFromDB;
                                }))
                .thenCompose(doctor -> {
                            final Profession professionToSave = createProfessionDto.toEntity();
                            return professionRepository.add(professionToSave)
                                    .thenApply(nth -> {
                                        doctor.getProfessions().add(professionToSave);
                                        return doctor;
                                    });
                        }
                ))
                .map(Doctor::toDetails);
    }

    public Flux<DoctorDetails> getAll() {

        return Mono.fromCompletionStage(doctorRepository.findAll())
                .flatMapMany(Flux::fromIterable)
                .map(Doctor::toDetails);
    }

    public Flux<DoctorDetails> getAllByIds(List<Long> ids) {

        return Mono.fromCompletionStage(doctorRepository.findAllById(ids))
                .flatMapMany(Flux::fromIterable)
                .map(Doctor::toDetails);
    }
}