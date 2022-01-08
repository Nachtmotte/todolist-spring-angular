package com.todolist.backend.controller.util;

import com.todolist.backend.dto.role.RoleDto;
import com.todolist.backend.dto.user.UserGetDto;
import com.todolist.backend.dto.user.UserPostDto;
import com.todolist.backend.entity.Role;
import com.todolist.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ModelMapperService {

    private final ModelMapper mapper;

    public User mapUserPostDtoToUserEntity(UserPostDto userPostDto) {
        return mapper.map(userPostDto, User.class);
    }

    public UserGetDto mapUserEntityToUserGetDto(User user) {
        return mapper.map(user, UserGetDto.class);
    }

    public List<UserGetDto> mapUserEntitiesToUserDtos(List<User> users) {
        return mapper.map(users, new TypeToken<List<UserGetDto>>() {
        }.getType());
    }

    public List<RoleDto> mapRoleEntitiesToRoleDtos(List<Role> roles) {
        return mapper.map(roles, new TypeToken<List<RoleDto>>() {
        }.getType());
    }
}
