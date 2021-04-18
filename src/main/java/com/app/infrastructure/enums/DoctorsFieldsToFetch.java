package com.app.infrastructure.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DoctorsFieldsToFetch {

    PROFESSIONS("professions");


    @Getter
    private final String fieldName;


}
