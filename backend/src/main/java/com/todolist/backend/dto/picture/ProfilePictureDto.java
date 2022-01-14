package com.todolist.backend.dto.picture;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class ProfilePictureDto {

    @NotBlank(message = "The Name is required.")
    @Size(max = 64, message = "The length of the Image name cannot exceed 64 characters")
    private String name;

    @NotBlank(message = "The Url is required.")
    @Size(max = 255, message = "The length of the Image URL cannot exceed 255 characters")
    private String url;

}
