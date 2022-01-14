package com.todolist.backend.controller.util;

import com.todolist.backend.dto.picture.ProfilePictureDto;
import com.todolist.backend.dto.role.RoleGetDto;
import com.todolist.backend.dto.user.*;
import com.todolist.backend.entity.ProfilePicture;
import com.todolist.backend.entity.Role;
import com.todolist.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
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

    public UserGetWithRolesDto mapUserEntityToUserGetWithRolesDto(User user) {
        return mapper.map(user, UserGetWithRolesDto.class);
    }

    public PageUserDto mapUsersPageToUsersPageDto(Page<User> users) {
        return mapper.map(users, PageUserDto.class);
    }

    public List<RoleGetDto> mapRoleEntitiesToRoleDtos(List<Role> roles) {
        return mapper.map(roles, new TypeToken<List<RoleGetDto>>() {
        }.getType());
    }

    public ProfilePicture mapProfilePictureDtoToProfilePictureEntity(ProfilePictureDto profilePictureDto) {
        return mapper.map(profilePictureDto, ProfilePicture.class);
    }

    public ProfilePictureDto mapProfilePictureEntityToProfilePictureDto(ProfilePicture profilePicture){
        return mapper.map(profilePicture, ProfilePictureDto.class);
    }
}
