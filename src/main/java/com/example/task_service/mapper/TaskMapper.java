package com.example.task_service.mapper;

import com.example.task_service.data.dto.request.TaskRequestDto;
import com.example.task_service.data.dto.response.TaskResponseDto;
import com.example.task_service.data.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "assignee.id", target = "assigneeId")
    TaskResponseDto toDto(Task task);

    Task toTask(TaskRequestDto taskRequestDto);
}
