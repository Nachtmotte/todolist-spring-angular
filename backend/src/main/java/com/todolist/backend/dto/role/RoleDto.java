package com.todolist.backend.dto.role;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RoleDto {

    @NotNull(message = "The Role is required.")
    private String name;
}
