package com.example.task_service.data.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@JsonPropertyOrder({"id", "name", "email"})
public class UserResponseDto {

    private UUID id;
    private String name;
    private String email;
}
