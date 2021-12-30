package com.todolist.backend.dto.user;

import com.todolist.backend.entity.ProfilePicture;
import lombok.*;

import java.sql.Timestamp;


@Data
@EqualsAndHashCode(callSuper = true)
public class UserGetDto extends UserDto {

    private Timestamp created;

    private Boolean verified;

    private ProfilePicture profilePicture;
}
