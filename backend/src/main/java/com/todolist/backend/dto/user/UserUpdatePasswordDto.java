package com.todolist.backend.dto.user;

import lombok.*;

import javax.validation.constraints.*;

@Data
public class UserUpdatePasswordDto {
    @NotBlank(message = "The Current Password is required.")
    @Size(min = 8, max = 45, message = "The Current Password is invalid")
    //It must include at least one number, uppercase and lowercase characters, without spaces.
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$", message = "The Current Password is invalid")
    private String currentPassword;

    @NotBlank(message = "The New Password is required.")
    @Size(min = 8, max = 45, message = "The length of the New Password must be at least 8 characters long and 45 as maximum")
    //It must include at least one number, uppercase and lowercase characters, without spaces.
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$", message = "The New Password is invalid")
    private String newPassword;
}
