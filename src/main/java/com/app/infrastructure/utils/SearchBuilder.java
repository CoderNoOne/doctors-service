package com.app.infrastructure.utils;

import com.app.application.dto.SearchByFieldValueDto;
import io.netty.util.internal.StringUtil;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchBuilder {

    private final Map<String, SearchByFieldValueDto<?>> commands;

    private SearchBuilder() {
        this.commands = new LinkedHashMap<>();
    }

    public static <T> SearchBuilder.SearchingSpec buildQuery(SearchByFieldValueDto<T> searchByFieldValueDto) {
        final SearchBuilder searchBuilder = new SearchBuilder();
        searchBuilder.commands.put("", searchByFieldValueDto);
        return new SearchingSpec(searchBuilder);
    }

    private SearchBuilder(Map<String, SearchByFieldValueDto<?>> updatedCommands) {
        this.commands = updatedCommands;
    }

    public String generateQuery() {

        return commands.entrySet()
                .stream()
                .map(e -> MessageFormat.format("{0} e.{1}={2}", e.getKey(), e.getValue().getFieldName(), e.getValue().getFieldValue()))
                .collect(Collectors.joining(" ", "where", ""));
    }

    public static class SearchingSpec {

        private final Map<String, SearchByFieldValueDto<?>> commands;

        private SearchingSpec(SearchBuilder builder) {
            this.commands = builder.commands;
        }

        public <T> SearchBuilder.SearchingSpec or(SearchByFieldValueDto<T> searchByFieldValueDto) {
            commands.put("or", searchByFieldValueDto);
            return SearchingSpec.this;
        }

        public <T> SearchBuilder.SearchingSpec and(SearchByFieldValueDto<T> searchByFieldValueDto) {
            commands.put("and", searchByFieldValueDto);
            return SearchingSpec.this;
        }

        public SearchBuilder end() {
            return new SearchBuilder(commands);
        }

    }


}

