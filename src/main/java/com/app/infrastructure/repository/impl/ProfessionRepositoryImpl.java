package com.app.infrastructure.repository.impl;

import com.app.application.exception.NotFoundException;
import com.app.application.exception.ProfessionAlreadyExistsException;
import com.app.domain.profession.Profession;
import com.app.domain.profession.ProfessionRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ProfessionRepositoryImpl implements ProfessionRepository {

    private static final String PROFESSION_NAME = "name";
    private final Stage.SessionFactory sessionFactory;

    @Override
    public Flux<Profession> findAllByNames(List<String> names) {

        return Flux.empty();
//        return Mono.fromCompletionStage(
//                databaseUtils
//                        .findByFieldValues(SearchByFieldValuesDto.<String>builder()
//                                        .fieldName(PROFESSION_NAME)
//                                        .fieldValues(names)
//                                        .build(),
//                                Profession.class,
//                                ProfessionFieldsToFetch.DOCTORS.getFieldName()))
//                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Profession> add(Profession profession) {

        return Mono.fromCompletionStage(sessionFactory.withTransaction((session, transaction) ->

                session.createQuery("select p from Profession p where p.name = :name", Profession.class)
                        .setParameter("name", profession.getName())
                        .getSingleResultOrNull()
                        .thenCompose(professionFromDB -> {
                            if (Objects.isNull(professionFromDB)) {
                                return session.persist(profession)
                                        .thenApply(nth -> profession);
                            }
                            throw new ProfessionAlreadyExistsException("There is already a profession with name: %s".formatted(professionFromDB.getName()));
                        })
        ));
    }

    @Override
    public Flux<Profession> addMany(List<Profession> items) {
        return null;
    }

    @Override
    public Flux<Profession> findAll() {
        return null;
    }

    @Override
    public Mono<Profession> findById(Long aLong) {
        return null;
    }

    @Override
    public Flux<Profession> findAllById(List<Long> longs) {
        return null;
    }

    @Override
    public Mono<Profession> deleteById(Long aLong) {
        return null;
    }

    @Override
    public Flux<Profession> deleteAllById(List<Long> longs) {
        return null;
    }

    @Override
    public Mono<Profession> findByName(String name) {

        return Mono.fromCompletionStage(
                sessionFactory.withSession(session -> session.createQuery("select p from Profession p where p.name = :name", Profession.class)
                        .setParameter("name", name)
                        .getSingleResultOrNull()
                        .thenCompose(x -> {
                            if (Objects.nonNull(x)) {
                                return session.fetch(x.getDoctors())
                                        .thenApply(z -> x);
                            }
                            throw new NotFoundException("No profession with name: %s".formatted(name));
                        })
                ));
    }
}
