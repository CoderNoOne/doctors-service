package com.app.application.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SearchByFieldValueDto<T> {

    private String name;
    private T value;
}
