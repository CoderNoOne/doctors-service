package com.app.application.dto;

import com.app.domain.doctor.Doctor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateDoctorDto {

    private String username;
    private String password;
    private String passwordConfirmation;

    private String firstName;
    private String lastName;

    private List<ProfessionDto> professions;

    public Doctor toEntity() {
        return Doctor.builder()
                .username(username)
                .password(password.toCharArray())
                .firstName(firstName)
                .lastName(lastName)
                .professions(Objects.nonNull(professions) ? professions.stream().map(ProfessionDto::toEntity).collect(Collectors.toList())  : new ArrayList<>())
                .build();
    }
}
