package com.example.task_service.service;

import com.example.task_service.data.Status;
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

    public List<TaskResponseDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::toDto)
                .toList();
    }

    public Optional<TaskResponseDto> findById(UUID id) {
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
    public TaskResponseDto updateAssignee(UUID taskId, UUID userId) {
        Task fetchedTask = loadTask(taskId);
        User fetchedUser = loadUser(userId);
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

    private User loadUser(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElseThrow(() -> new EntityNotFoundException("No such user"));
    }

    @Transactional(readOnly = false)
    public TaskResponseDto updateStatus(UUID taskId, Status status) {
        Task fetchedTask = repository.findById(taskId).orElseThrow();
        fetchedTask.setStatus(status);
        Task updatedTask = repository.save(fetchedTask);
        return mapper.toDto(updatedTask);
    }
}
