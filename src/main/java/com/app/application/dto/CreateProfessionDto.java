package com.app.application.dto;

import com.app.domain.profession.Profession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateProfessionDto {

    private String name;

    public Profession toEntity() {
        return Profession.builder()
                .name(name)
                .doctors(Collections.emptySet())
                .build();
    }
}
