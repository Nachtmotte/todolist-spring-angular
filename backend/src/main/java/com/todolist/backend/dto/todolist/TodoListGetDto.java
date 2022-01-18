package com.todolist.backend.dto.todolist;

import lombok.*;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = true)
public class TodoListGetDto extends TodoListDto {

    private Integer id;

    private Timestamp created;
}
