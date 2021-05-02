package com.app.application.dto;

import com.app.domain.patient.Patient;
import com.app.domain.patient.embeddable.FavoriteDoctor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreatePatientDto {

    private Long id;
    private String username;

    private String firstName;
    private String lastName;
    private Integer age;
    private Set<FavoriteDoctor> favoriteDoctors;

    public Patient toEntity() {
        return Patient.builder()
                .id(id)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .age(age)
                .favoriteDoctors(Objects.nonNull(favoriteDoctors) ? favoriteDoctors : Collections.emptySet())
                .build();
    }
}
