package com.todolist.backend.dto.user;

import lombok.*;

import javax.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserPostDto extends UserDto {
    @NotBlank(message = "The Password is required.")
    @Size(min = 8, max = 45, message = "The length of the Password must be at least 8 characters long and 45 as maximum")
    //It must include at least one number, uppercase and lowercase characters, without spaces.
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$", message = "The Password is invalid")
    private String password;
}
