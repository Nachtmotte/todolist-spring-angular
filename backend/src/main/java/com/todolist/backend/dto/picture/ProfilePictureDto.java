package com.todolist.backend.dto.picture;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class ProfilePictureDto {

    @Size(max = 64, message = "The length of the Image name cannot exceed 64 characters")
    private String name;

    @Size(max = 255, message = "The length of the Image URL cannot exceed 255 characters")
    @Pattern(regexp = "(https://firebasestorage.googleapis.com/)(.*)(\\?.*)", message = "The Image URL is invalid.")
    private String url;

}
