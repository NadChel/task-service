package com.example.task_service.data.dto.response;

import com.example.task_service.data.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TaskResponseDto {

    private UUID id;
    private String name;
    private String description;
    private UUID assigneeId;
    private Status status;
}


