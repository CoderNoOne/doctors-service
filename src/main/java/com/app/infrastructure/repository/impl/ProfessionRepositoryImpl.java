package com.app.infrastructure.repository.impl;

import com.app.application.dto.SearchByFieldValueDto;
import com.app.application.dto.SearchByFieldValuesDto;
import com.app.application.exception.NotFoundException;
import com.app.domain.doctor.Doctor;
import com.app.domain.profession.Profession;
import com.app.domain.profession.ProfessionRepository;
import com.app.infrastructure.enums.ProfessionFieldsToFetch;
import com.app.infrastructure.utils.DatabaseUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

@Repository
@RequiredArgsConstructor
public class ProfessionRepositoryImpl implements ProfessionRepository {

    private static final String PROFESSION_NAME = "name";
    private final DatabaseUtils databaseUtils;
    private final Stage.SessionFactory sessionFactory;

    @Override
    public CompletionStage<List<Profession>> findAllByNames(List<String> names) {

        return
                databaseUtils
                        .findAllByFieldValues(SearchByFieldValuesDto.<String>builder()
                                        .fieldName(PROFESSION_NAME)
                                        .fieldValues(names)
                                        .build(),
                                Profession.class,
                                ProfessionFieldsToFetch.DOCTORS.getFieldName());

    }

    @Override
    public CompletionStage<Profession> add(Profession profession) {

        return databaseUtils.saveEntity(profession);

//        return sessionFactory.withTransaction((session, transaction) ->
//
//                session.createQuery("select p from Profession p where p.name = :name", Profession.class)
//                        .setParameter("name", profession.getName())
//                        .getSingleResultOrNull()
//                        .thenCompose(professionFromDB -> {
//                            if (Objects.isNull(professionFromDB)) {
//                                return session.persist(profession)
//                                        .thenApply(nth -> profession);
//                            }
//                            throw new ProfessionAlreadyExistsException("There is already a profession with name: %s".formatted(professionFromDB.getName()));
//                        })
//        );
    }

    @Override
    public CompletionStage<List<Profession>> addMany(List<Profession> professions) {
        return databaseUtils.saveEntities(professions);
    }

    @Override
    public CompletionStage<List<Profession>> findAll() {
        return databaseUtils.findAll(Profession.class, ProfessionFieldsToFetch.DOCTORS.getFieldName());
    }

    @Override
    public CompletionStage<Profession> findById(Long id) {

        return databaseUtils.findOneByFieldValue(
                SearchByFieldValueDto.<Long>builder()
                        .fieldValue(id)
                        .fieldName("id")
                        .build(),
                Profession.class,
                ProfessionFieldsToFetch.DOCTORS.getFieldName()
        );

//        return sessionFactory.withSession(
//                session -> session
//                        .createQuery("select p from Profession p where p.id = :id", Profession.class)
//                        .setParameter("id", id)
//                        .getSingleResultOrNull()
//                        .thenCompose(profession -> session.fetch(Objects.nonNull(profession) ? profession.getDoctors() : null).thenApply(y -> profession)));
    }

    @Override
    public CompletionStage<List<Profession>> findAllById(List<Long> longs) {
        return null;
    }

    @Override
    public CompletionStage<Profession> deleteById(Long aLong) {
        return null;
    }

    @Override
    public CompletionStage<List<Profession>> deleteAllById(List<Long> longs) {
        return null;
    }

    @Override
    public CompletionStage<Profession> findByName(String name) {

        return
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
                );
    }

    @Override
    public CompletionStage<List<Profession>> findAllByDoctorId(Long id) {

        return sessionFactory.withSession(
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
                                ));

    }

    @Override
    public CompletionStage<Boolean> doExistsByName(String name) {
        return databaseUtils.doExistsByFieldValue(SearchByFieldValueDto.<String>builder()
                        .fieldName("name")
                        .fieldValue(name)
                        .build(),
                Profession.class);
    }
}
