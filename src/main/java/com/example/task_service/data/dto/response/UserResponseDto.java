package com.example.task_service.data.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponseDto {

    private UUID id;
    private String name;
    private String email;
}
