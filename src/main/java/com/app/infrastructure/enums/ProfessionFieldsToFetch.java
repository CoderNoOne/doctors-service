package com.app.infrastructure.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ProfessionFieldsToFetch {

    DOCTORS("doctors");

    @Getter
    private final String fieldName;

}
