package com.todolist.backend.dto.todolist;

import com.todolist.backend.dto.user.UserGetDto;
import lombok.*;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = true)
public class TodoListGetDto extends TodoListDto {

    private Integer id;

    private Timestamp created;
}
