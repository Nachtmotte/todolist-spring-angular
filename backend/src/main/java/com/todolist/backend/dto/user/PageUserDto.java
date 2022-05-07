package com.todolist.backend.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class PageUserDto {

    private boolean first;

    private boolean last;

    private int number;

    private int numberOfElements;

    private int size;

    private int totalPages;

    private long totalElements;

    private List<UserGetWithRolesDto> content;
}
