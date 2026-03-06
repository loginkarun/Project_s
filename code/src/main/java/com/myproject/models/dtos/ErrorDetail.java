package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Error Detail
 * Maps to ErrorDetail schema in OpenAPI specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail {

    @JsonProperty("field")
    private String field;

    @JsonProperty("issue")
    private String issue;
}