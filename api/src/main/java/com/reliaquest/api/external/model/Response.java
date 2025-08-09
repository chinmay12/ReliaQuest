package com.reliaquest.api.external.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Response<T>(T data, Status status, String error) {

    public enum Status {
        HANDLED("Successfully processed request."),
        ERROR("Failed to process request.");

        @JsonValue
        @Getter
        private final String value;

        Status(String value) {
            this.value = value;
        }
    }
}
