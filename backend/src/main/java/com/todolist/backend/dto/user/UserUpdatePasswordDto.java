package com.todolist.backend.dto.user;

import lombok.*;
import javax.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserUpdatePasswordDto extends UserDto{
    @NotBlank(message = "The Current Password is required.")
    @Size(min = 8, max = 45, message = "The Current Password is invalid")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$", message = "The Current Password is invalid")
    private String currentPassword;

    @Size(min = 8, max = 45, message = "The length of the New Password must be at least 8 characters long and 45 as maximum")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$", message = "The New Password is invalid")
    private String newPassword;
}
