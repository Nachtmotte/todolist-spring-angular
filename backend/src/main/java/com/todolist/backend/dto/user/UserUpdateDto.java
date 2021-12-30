package com.todolist.backend.dto.user;

import lombok.*;

import javax.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserUpdateDto extends UserDto {
    @NotBlank(message = "The Current Password is required.")
    @Size(min = 8, max = 45, message = "The Current Password is invalid")
    //It must include at least one number, uppercase and lowercase characters, without spaces.
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$", message = "The Current Password is invalid")
    private String currentPassword;
}
