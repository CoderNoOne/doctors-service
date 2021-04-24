package com.app.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetDoctorDto {

    private Long id;
    private String username;
    private String password;
    private Role role;

}
