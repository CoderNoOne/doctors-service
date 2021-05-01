package com.app.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DoctorLoginDetailsDto {

    private Long id;
    private String username;
    private String password;
}
