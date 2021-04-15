package com.app.application.dto;

import com.app.domain.profession.Profession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class ProfessionDto {


    private Long id;
    private String name;

    public Profession toEntity() {
        return Profession.builder()
                .name(name)
                .build();
    }
}
