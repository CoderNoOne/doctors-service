package com.app.application.service;

import com.app.application.dto.CreateDoctorDto;
import com.app.application.dto.CreateProfessionDto;
import com.app.application.dto.DoctorDetails;
import com.app.application.dto.ProfessionDto;
import com.app.application.exception.NotFoundException;
import com.app.domain.doctor.Doctor;
import com.app.domain.profession.Profession;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final Stage.SessionFactory sessionFactory;

    public Mono<DoctorDetails> saveDoctor(final CreateDoctorDto createDoctorDto) {


        return Mono.fromCompletionStage(sessionFactory.withTransaction((session, transaction) ->

                {
                    final Doctor doctorToSave = createDoctorDto.toEntity();
                    final List<String> professionsNames = createDoctorDto.getProfessions().stream().map(ProfessionDto::getName).collect(Collectors.toList());

                    final CompletionStage<List<Profession>> professionsFromDb = getExistingProfessionsFromDB(session, professionsNames);

                    return professionsFromDb
                            .thenCompose(professions -> {
                                professionsNames.removeIf(pn -> professions.stream().anyMatch(prDB -> prDB.getName().equals(pn)));
                                final List<Profession> professionsToSave = professionsNames.stream().map(name -> Profession.builder().name(name).build()).collect(Collectors.toList());
                                final List<Profession> updatedProfessions = new ArrayList<>();


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
                session ->
                        session.find(Doctor.class, id)
                                .thenCompose(d -> {
                                            if (Objects.nonNull(d)) {
                                                return session
                                                        .createQuery("select p from Profession p where :d member of p.doctors", Profession.class)
                                                        .setParameter("d", d)
                                                        .getResultList();
                                            }
                                            throw new NotFoundException("No doctor with id: %d".formatted(id));
                                        }
                                )))
                .flatMapMany(Flux::fromIterable)
                .map(Profession::toDto);

    }

    public Flux<DoctorDetails> saveDoctors(final List<CreateDoctorDto> createDoctorDtoList) {

        return Mono.fromCompletionStage(sessionFactory.withTransaction((session, transaction) -> {

            final Map<CreateDoctorDto, List<ProfessionDto>> professionsGroupedByDoctor = createDoctorDtoList
                    .stream()
                    .collect(Collectors.groupingBy(
                            Function.identity(),
                            Collectors.flatMapping(dto -> Objects.isNull(dto.getProfessions()) ? Stream.empty() : dto.getProfessions().stream(), Collectors.toList()))
                    );

            final List<String> allProfessionsName = professionsGroupedByDoctor.values().stream().mapMulti(Iterable::forEach).map(obj -> obj instanceof ProfessionDto p ? p : null).filter(Objects::nonNull).map(ProfessionDto::getName).collect(Collectors.toList());
            final CompletionStage<List<Profession>> professionsFromDb = getExistingProfessionsFromDB(session, allProfessionsName);

            return professionsFromDb
                    .thenCompose(professions -> {
                        allProfessionsName.removeIf(pn -> professions.stream().anyMatch(prDB -> prDB.getName().equals(pn)));
                        final List<Profession> professionsToSave = allProfessionsName.stream().map(name -> Profession.builder().name(name).build()).distinct().collect(Collectors.toList());
                        final List<Profession> updatedProfessions = new ArrayList<>();

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

    private CompletionStage<List<Profession>> getExistingProfessionsFromDB(Stage.Session session, List<String> professionsNames) {

        final CriteriaQuery<Profession> query = sessionFactory.getCriteriaBuilder().createQuery(Profession.class);
        final Root<Profession> profession = query.from(Profession.class);

        return session.createQuery(query.where(profession.get("name").in(professionsNames))).getResultList();
    }

    public Mono<DoctorDetails> addProfessionForDoctor(final CreateProfessionDto createProfessionDto, final Long doctorId) {


        return Mono.fromCompletionStage(sessionFactory.withTransaction(

                (session, transaction) ->
                        session.createQuery("select d from Doctor d where d.id = :id", Doctor.class)
                                .setParameter("id", doctorId)
                                .getSingleResultOrNull()
                                .thenApply(doctorFromDB -> {
                                    if (Objects.nonNull(doctorFromDB)) {
                                        return doctorFromDB;
                                    }
                                    throw new NotFoundException("No doctor with id: %d".formatted(doctorId));
                                })
                                .thenCompose(doctor -> session.fetch(doctor.getProfessions()).thenApply(professions -> doctor))
                                .thenApply(doctor -> {
                                    if (doctor.getProfessions().stream().anyMatch(profession -> profession.getName().equals(createProfessionDto.getName()))) {
                                        throw new NotFoundException("Doctor already has the profession: %s".formatted(createProfessionDto.getName()));
                                    }
                                    return doctor;
                                })
                                .thenCompose(doctorFromDB ->
                                        session.createQuery("select p from Profession p where p.name = :name", Profession.class)
                                                .setParameter("name", createProfessionDto.getName())
                                                .getSingleResultOrNull()
                                                .thenCompose(professionFromDB -> {
                                                    if (Objects.nonNull(professionFromDB)) {
                                                        doctorFromDB.getProfessions().add(professionFromDB);
                                                    }
                                                    return CompletableFuture.completedFuture(doctorFromDB);
                                                }))
                                .thenCompose(doctor -> {
                                            final Profession professionToSave = createProfessionDto.toEntity();
                                            return session.persist(professionToSave)
                                                    .thenApply(nth -> {
                                                        doctor.getProfessions().add(professionToSave);
                                                        return doctor;
                                                    });
                                        }
                                )))
                .map(Doctor::toDetails);
    }
}