package com.app.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateDoctorDto {

    private String firstName;
    private String lastName;

    private List<ProfessionDto> professions;

    private String password;
    private String passwordConfirmation;

}
