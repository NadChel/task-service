package com.example.task_service.data.event;

import com.example.task_service.data.dto.response.TaskResponseDto;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"type", "task"})
public class TaskEvent {

    private TaskEventType type;
    private TaskResponseDto task;

    public static TaskEvent created(TaskResponseDto task) {
        return of(TaskEventType.CREATED, task);
    }

    public static TaskEvent assigned(TaskResponseDto task) {
        return of(TaskEventType.ASSIGNED, task);
    }

    private static TaskEvent of(TaskEventType type, TaskResponseDto task) {
        TaskEvent event = new TaskEvent();
        event.setType(type);
        event.setTask(task);
        return event;
    }

    public enum TaskEventType {

        CREATED, ASSIGNED
    }
}
