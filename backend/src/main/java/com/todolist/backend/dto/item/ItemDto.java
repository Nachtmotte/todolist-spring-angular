package com.todolist.backend.dto.item;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class ItemDto {

    @NotBlank(message = "The Text of the Item is required.")
    @Size(max = 255, message = "The length of the Name cannot exceed 255 characters")
    private String text;
}
