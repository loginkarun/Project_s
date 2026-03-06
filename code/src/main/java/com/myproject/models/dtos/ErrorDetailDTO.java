package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing error detail
 * Maps to ErrorDetail schema in OpenAPI
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetailDTO {

    @JsonProperty("field")
    private String field;

    @JsonProperty("issue")
    private String issue;
}
