package com.todolist.backend.dto.user;

import com.todolist.backend.dto.picture.ProfilePictureDto;
import lombok.*;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserGetDto extends UserDto {

    private Integer id;

    private Timestamp created;

    private Boolean verified;

    private ProfilePictureDto profilePicture;
}
