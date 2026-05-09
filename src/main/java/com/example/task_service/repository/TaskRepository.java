package com.example.task_service.repository;

import com.example.task_service.data.entity.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RepositoryDefinition(idClass = UUID.class, domainClass = Task.class)
public interface TaskRepository {

    List<Task> findAll(Pageable pageable);

    Optional<Task> findById(UUID id);

    Task save(Task task);
}
