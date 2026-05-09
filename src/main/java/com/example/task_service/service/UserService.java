package com.example.task_service.service;

import com.example.task_service.data.dto.request.UserRequestDto;
import com.example.task_service.data.dto.response.UserResponseDto;
import com.example.task_service.data.entity.User;
import com.example.task_service.mapper.UserMapper;
import com.example.task_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional(readOnly = false)
    public UserResponseDto save(UserRequestDto userRequestDto) {
        User savedUser = repository.save(mapper.toUser(userRequestDto));
        return mapper.toDto(savedUser);
    }
}
