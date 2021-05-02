package com.app.application.dto;

import com.app.domain.patient.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;

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

    public Patient toEntity() {
        return Patient.builder()
                .id(id)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .age(age)
                .favoriteDoctors(Collections.emptySet())
                .build();
    }
}
