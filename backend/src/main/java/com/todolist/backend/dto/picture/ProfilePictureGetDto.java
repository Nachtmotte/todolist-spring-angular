package com.todolist.backend.dto.picture;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProfilePictureGetDto extends ProfilePictureDto {

    private Integer userId;
}
