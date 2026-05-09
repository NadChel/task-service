package com.example.task_service.service;

import com.example.task_service.data.dto.request.AssigneeRequestDto;
import com.example.task_service.data.dto.request.StatusRequestDto;
import com.example.task_service.data.dto.request.TaskRequestDto;
import com.example.task_service.data.dto.response.TaskResponseDto;
import com.example.task_service.data.entity.Task;
import com.example.task_service.data.entity.User;
import com.example.task_service.data.event.TaskEvent;
import com.example.task_service.mapper.TaskMapper;
import com.example.task_service.repository.TaskRepository;
import com.example.task_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository repository;
    private final UserRepository userRepository;
    private final TaskMapper mapper;
    private final KafkaTemplate<UUID, TaskEvent> kafkaTemplate;
    private static final String TASKS_TOPIC = "tasks";

    public List<TaskResponseDto> findTasks(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::toDto)
                .toList();
    }

    public Optional<TaskResponseDto> findTask(UUID id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Transactional(readOnly = false)
    public TaskResponseDto save(TaskRequestDto taskRequestDto) {
        Task task = repository.save(mapper.toTask(taskRequestDto));
        TaskResponseDto taskResponseDto = mapper.toDto(task);
        kafkaTemplate.send(TASKS_TOPIC, TaskEvent.created(taskResponseDto));
        return taskResponseDto;
    }

    @Transactional(readOnly = false)
    public TaskResponseDto updateAssignee(UUID taskId, AssigneeRequestDto assignee) {
        Task fetchedTask = loadTask(taskId);
        User fetchedUser = loadUser(assignee);
        fetchedTask.setAssignee(fetchedUser);
        Task updatedTask = repository.save(fetchedTask);
        TaskResponseDto taskResponseDto = mapper.toDto(updatedTask);
        kafkaTemplate.send(TASKS_TOPIC, TaskEvent.assigned(taskResponseDto));
        return taskResponseDto;
    }

    private Task loadTask(UUID taskId) {
        Optional<Task> taskOptional = repository.findById(taskId);
        return taskOptional.orElseThrow(() -> new EntityNotFoundException("No such task"));
    }

    private User loadUser(AssigneeRequestDto assignee) {
        Optional<User> userOptional = userRepository.findById(assignee.getUserId());
        return userOptional.orElseThrow(() -> new EntityNotFoundException("No such user"));
    }

    @Transactional(readOnly = false)
    public TaskResponseDto updateStatus(UUID taskId, StatusRequestDto status) {
        Task fetchedTask = repository.findById(taskId).orElseThrow();
        fetchedTask.setStatus(status.getStatus());
        Task updatedTask = repository.save(fetchedTask);
        return mapper.toDto(updatedTask);
    }
}
