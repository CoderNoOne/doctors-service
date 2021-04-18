package com.app.infrastructure.utils;

import com.app.application.dto.FieldToFetchDto;
import com.app.application.dto.SearchByFieldValueDto;
import com.app.application.dto.SearchByFieldValuesDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.persistence.criteria.*;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseUtils {

    @Getter
    private final Stage.SessionFactory sessionFactory;

    public void close() {
        if (Objects.nonNull(sessionFactory)) {
            sessionFactory.close();
        }
    }

    @PreDestroy
    public void closeSessionFactory() {
        close();
        log.info("Session factory closed");
    }

    public <T> CompletionStage<T> doInTx(BiFunction<Stage.Session, Stage.Transaction, CompletionStage<T>> action) {
        return sessionFactory.withTransaction(action);
    }

    public <T> CompletionStage<T> doInStatelessSession(Function<Stage.StatelessSession, CompletionStage<T>> action) {
        return sessionFactory.withStatelessSession(action);
    }

    public <T> CompletionStage<T> doInSession(Function<Stage.Session, CompletionStage<T>> action) {
        return sessionFactory.withSession(action);
    }

    public <T> CompletionStage<T> saveEntity(T entity) {

        return
                doInTx((session, tx) -> session.persist(entity)
                        .thenApply((returnVal -> entity)));
    }

    public <T> CompletionStage<List<T>> saveEntities(List<T> items) {
        return doInTx((session, tx) -> session.persist(items.toArray())
                .thenApply(returnValues -> items)
        );
    }

    public <T, E> CompletionStage<List<T>> findByFieldValue(SearchByFieldValueDto<E> field, Class<T> entityClass) {

        return doInStatelessSession(session -> session.createQuery(MessageFormat.format("select e from {0} e where e.{1}= :{1}", entityClass.getSimpleName(), field.getFieldName()), entityClass)
                .setParameter(field.getFieldName(), field.getFieldValue())
                .getResultList());
    }


    public <T, E> CompletionStage<T> findOneByFieldValue(SearchByFieldValueDto<E> field, Class<T> entityClass) {

        return doInSession((session -> session.createQuery(MessageFormat.format("select e from {0} e where e.{1}= :{1}", entityClass.getSimpleName(), field.getFieldName()), entityClass)
                .setParameter(field.getFieldName(), field.getFieldValue())
                .getSingleResultOrNull()));
    }

    public <T, E, R> CompletionStage<R> findOneByFieldValue(SearchByFieldValueDto<E> field, Class<T> entityClass, String fieldToFetch, Function<T, R> mapper) {

        return doInSession((session) -> session.createQuery(MessageFormat.format("select distinct e from {0} e left join fetch e.{1} where e.{2}= :{2} ", entityClass.getSimpleName(), fieldToFetch, field.getFieldName()), entityClass)
                .setParameter(field.getFieldName(), field.getFieldValue())
                .getSingleResultOrNull()
                .thenApply(mapper));
    }

    public <T, E> CompletionStage<List<T>> findByFieldValues(SearchByFieldValuesDto<E> field, Class<T> entityClass, String... fieldsToFetch) {

        final var fetchCommands = Optional.ofNullable(fieldsToFetch)
                .map(Arrays::stream)
                .map(stream -> stream
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.joining(" left join fetch e.", " left join fetch e.", "")))
                .orElse("");

        return doInSession(session -> session.createQuery(MessageFormat.format("select distinct e from {0} e {1} where e.{2} in (:{2})", entityClass.getSimpleName(), fetchCommands, field.getFieldName()), entityClass)
                .setParameter(field.getFieldName(), field.getFieldValues())
                .getResultList());
    }

}
