package com.app.application.dto;

import com.app.application.dto.type.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
