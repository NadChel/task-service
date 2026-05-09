package com.example.task_service.mapper;

import com.example.task_service.data.dto.request.UserRequestDto;
import com.example.task_service.data.dto.response.UserResponseDto;
import com.example.task_service.data.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserRequestDto userRequestDto);

    UserResponseDto toDto(User user);
}
