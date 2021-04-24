package com.app.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DoctorDetails {

    private Long id;

    private String firstName;
    private String lastName;

    private List<ProfessionDto> professions;
}
