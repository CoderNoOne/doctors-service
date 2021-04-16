package com.app.domain.generic;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CrudRepository<T, ID> {
    Mono<T> add(T item);

    Flux<T> addMany(List<T> items);

    Flux<T> findAll();

    Mono<T> findById(ID id);

    Flux<T> findAllById(List<ID> ids);

    Mono<T> deleteById(ID id);

    Flux<T> deleteAllById(List<ID> ids);
}
