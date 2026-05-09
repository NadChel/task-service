package com.example.task_service.data.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@JsonPropertyOrder({"errorMessage", "timestamp"})
public class ErrorResponseDto {

    private String errorMessage;
    private Instant timestamp;

    public ErrorResponseDto() {
        timestamp = Instant.now();
    }

    public static ErrorResponseDto from(Throwable throwable) {
        ErrorResponseDto response = new ErrorResponseDto();
        response.setErrorMessage(throwable.getMessage());
        return response;
    }
}
