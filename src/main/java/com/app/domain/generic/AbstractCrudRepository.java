package com.app.domain.generic;

import com.app.infrastructure.utils.DatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class AbstractCrudRepository<T, ID> implements CrudRepository<T, ID> {

    protected DatabaseUtils databaseUtils;

    @Autowired
    public void setConnectionUtils(DatabaseUtils databaseUtils) {
        this.databaseUtils = databaseUtils;
    }

    @Override
    public Mono<T> add(T item) {
        return Mono.fromCompletionStage(databaseUtils.saveEntity(item));
    }

    @Override
    public Flux<T> addMany(List<T> items) {
        return Mono.fromCompletionStage(databaseUtils.saveEntities(items))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<T> findAll() {
        return null;
    }

    @Override
    public Mono<T> findById(ID id) {
        return null;
    }

    @Override
    public Flux<T> findAllById(List<ID> ids) {
        return null;
    }

    @Override
    public Mono<T> deleteById(ID id) {
        return null;
    }

    @Override
    public Flux<T> deleteAllById(List<ID> ids) {
        return null;
    }

}
