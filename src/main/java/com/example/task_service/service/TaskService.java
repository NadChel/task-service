package com.example.task_service.service;

import com.example.task_service.data.dto.request.AssigneeRequestDto;
import com.example.task_service.data.dto.request.StatusRequestDto;
import com.example.task_service.data.dto.request.TaskRequestDto;
import com.example.task_service.data.dto.response.TaskResponseDto;
import com.example.task_service.data.entity.Task;
import com.example.task_service.data.entity.User;
import com.example.task_service.mapper.TaskMapper;
import com.example.task_service.repository.TaskRepository;
import com.example.task_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
        return mapper.toDto(task);
    }

    @Transactional(readOnly = false)
    public TaskResponseDto updateAssignee(UUID taskId, AssigneeRequestDto assignee) {
        Task fetchedTask = repository.findById(taskId).orElseThrow();
        User fetchedUser = userRepository.findById(assignee.getUserId()).orElseThrow();
        fetchedTask.setAssignee(fetchedUser);
        Task updatedTask = repository.save(fetchedTask);
        return mapper.toDto(updatedTask);
    }

    @Transactional(readOnly = false)
    public TaskResponseDto updateStatus(UUID taskId, StatusRequestDto status) {
        Task fetchedTask = repository.findById(taskId).orElseThrow();
        fetchedTask.setStatus(status.getStatus());
        Task updatedTask = repository.save(fetchedTask);
        return mapper.toDto(updatedTask);
    }
}
