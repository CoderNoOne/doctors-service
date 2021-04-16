package com.app.application.service;

import com.app.application.dto.CreateDoctorDto;
import com.app.application.dto.DoctorDetails;
import com.app.application.dto.DoctorDto;
import com.app.application.dto.ProfessionDto;
import com.app.application.exception.NotFoundException;
import com.app.domain.doctor.Doctor;
import com.app.domain.doctor.DoctorRepository;
import com.app.domain.profession.Profession;
import com.app.domain.profession.ProfessionRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final Stage.SessionFactory sessionFactory;
    private final ProfessionRepository professionRepository;

    public Mono<DoctorDetails> saveDoctor(final CreateDoctorDto createDoctorDto) {


        return Mono.fromCompletionStage(sessionFactory.withTransaction((session, transaction) ->

                {
                    final Doctor doctorToSave = createDoctorDto.toEntity();
                    final List<String> professionsNames = createDoctorDto.getProfessions().stream().map(ProfessionDto::getName).collect(Collectors.toList());
                    final CriteriaQuery<Profession> query = sessionFactory.getCriteriaBuilder().createQuery(Profession.class);
                    final Root<Profession> profession = query.from(Profession.class);

                    final CompletionStage<List<Profession>> professionsFromDb = session.createQuery(query.where(profession.get("name").in(professionsNames))).getResultList();

                    return professionsFromDb
                            .thenCompose(professions -> {
                                professionsNames.removeIf(pn -> professions.stream().anyMatch(prDB -> prDB.getName().equals(pn)));
                                final List<Profession> professionsToSave = professionsNames.stream().map(name -> Profession.builder().name(name).build()).collect(Collectors.toList());
                                List<Profession> updatedProfessions = new ArrayList<>();


                                return session.persist(professionsToSave.toArray())
                                        .thenApply(nth -> {
                                            updatedProfessions.addAll(professionsToSave);
                                            updatedProfessions.addAll(professions);
                                            return updatedProfessions;
                                        })
                                        .thenApply(allProfessionsFromDB -> {
                                            doctorToSave.setProfessions(allProfessionsFromDB);
                                            return doctorToSave;
                                        })
                                        .thenCompose(doctor -> session.persist(doctor)
                                                .thenApply(nth -> doctor)
                                        );
                            });

                }

        ))
                .map(Doctor::toDetails);
    }

    public Mono<DoctorDetails> getDoctorByIdWithFetchedProfessions(final Long id) {

        return Mono.fromCompletionStage(sessionFactory.withSession(
                session -> session
                        .find(Doctor.class, id)
                        .thenCompose(doctor -> {
                            if (Objects.nonNull(doctor)) {
                                return session
                                        .fetch(doctor.getProfessions())
                                        .thenApply(professions -> doctor);
                            }
                            throw new NotFoundException("No doctor with id %d".formatted(id));
                        })))
                .map(Doctor::toDetails);

    }

    public Flux<ProfessionDto> getDoctorProfessionsByDoctorId(final Long id) {

        return Mono.fromCompletionStage(sessionFactory.withSession(
                session -> session
                        .createQuery("select d from Doctor d join fetch d.professions where d.id = :id", Doctor.class)
                        .setParameter("id", id)
                        .getSingleResultOrNull()))
                .switchIfEmpty(Mono.error(() -> new NotFoundException("No doctor with id: %d".formatted(id))))
                .map(Doctor::getProfessions)
                .flatMapMany(Flux::fromIterable)
                .map(Profession::toDto);

    }

    public Flux<DoctorDetails> saveDoctors(final List<CreateDoctorDto> createDoctorDtoList) {

        return Mono.fromCompletionStage(sessionFactory.withTransaction((session, transaction) -> {

            final Map<CreateDoctorDto, List<ProfessionDto>> professionsGroupedByDoctor = createDoctorDtoList
                    .stream()
                    .collect(Collectors.groupingBy(
                            dto -> dto,
                            Collectors.flatMapping(dto -> Objects.isNull(dto.getProfessions()) ? Stream.empty() : dto.getProfessions().stream(), Collectors.toList()))
                    );

            final List<String> allProfessionsName = professionsGroupedByDoctor.values().stream().flatMap(Collection::stream).map(ProfessionDto::getName).collect(Collectors.toList());

            final CriteriaQuery<Profession> query = sessionFactory.getCriteriaBuilder().createQuery(Profession.class);
            final Root<Profession> profession = query.from(Profession.class);

            final CompletionStage<List<Profession>> professionsFromDb = session.createQuery(query.where(profession.get("name").in(allProfessionsName))).getResultList();

            return professionsFromDb
                    .thenCompose(professions -> {
                        allProfessionsName.removeIf(pn -> professions.stream().anyMatch(prDB -> prDB.getName().equals(pn)));
                        final List<Profession> professionsToSave = allProfessionsName.stream().map(name -> Profession.builder().name(name).build()).collect(Collectors.toList());
                        List<Profession> updatedProfessions = new ArrayList<>();

                        return session.persist(professionsToSave.toArray())
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
                                                    final Doctor doctor = e.getKey().toEntity();
                                                    doctor.setProfessions(e.getValue());
                                                    return doctor;
                                                })
                                                .collect(Collectors.toList()))
                                .thenCompose(doctors -> session.persist(doctors.toArray())
                                        .thenApply(nth -> doctors)
                                );
                    });

        }))
                .flatMapMany(Flux::fromIterable)
                .map(Doctor::toDetails);
    }
}
