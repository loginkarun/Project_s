package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO representing error response
 * Maps to ErrorResponse schema in OpenAPI
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    @NotNull(message = "Timestamp cannot be null")
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @NotNull(message = "Trace ID cannot be null")
    @JsonProperty("traceId")
    private String traceId;

    @NotNull(message = "Error code cannot be null")
    @JsonProperty("errorCode")
    private String errorCode;

    @NotNull(message = "Message cannot be null")
    @JsonProperty("message")
    private String message;

    @JsonProperty("details")
    @Builder.Default
    private List<ErrorDetailDTO> details = new ArrayList<>();
}
