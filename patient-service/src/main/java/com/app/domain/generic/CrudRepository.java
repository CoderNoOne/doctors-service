package com.app.domain.generic;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface CrudRepository<T, ID> {
    CompletionStage<T> add(T item);

    CompletionStage<List<T>> addMany(List<T> items);

    CompletionStage<List<T>> findAll();

    CompletionStage<T> findById(ID id);

    CompletionStage<List<T>> findAllById(List<ID> ids);

    CompletionStage<T> deleteById(ID id);

    CompletionStage<List<T>> deleteAllById(List<ID> ids);

}
