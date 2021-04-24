package com.app.domain.profession;

import com.app.domain.generic.CrudRepository;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface ProfessionRepository extends CrudRepository<Profession, Long> {

    CompletionStage<Profession> findByName(String name);

    CompletionStage<List<Profession>> findAllByNames(List<String> names);

    CompletionStage<List<Profession>> findAllByDoctorId(Long id);

    CompletionStage<Boolean> doExistsByName(String name);
}
