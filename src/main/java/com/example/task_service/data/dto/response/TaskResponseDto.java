package com.example.task_service.data.dto.response;

import com.example.task_service.data.Status;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@JsonPropertyOrder({"id", "name", "status", "assigneeId", "description"})
public class TaskResponseDto {

    private UUID id;
    private String name;
    private Status status;
    private UUID assigneeId;
    private String description;
}


