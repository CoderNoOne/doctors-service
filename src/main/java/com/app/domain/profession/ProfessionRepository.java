package com.app.domain.profession;

import com.app.domain.generic.CrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProfessionRepository extends CrudRepository<Profession, Long> {

    Mono<Profession> findByName(String name);

    Flux<Profession> findAllByNames(List<String> names);

}
