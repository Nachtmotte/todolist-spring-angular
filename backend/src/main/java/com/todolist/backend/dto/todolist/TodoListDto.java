package com.todolist.backend.dto.todolist;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class TodoListDto {

    @NotBlank(message = "The Name of the TodoList is required.")
    @Size(min = 1, max = 45, message = "The length of the Name cannot exceed 45 characters")
    private String name;
}
