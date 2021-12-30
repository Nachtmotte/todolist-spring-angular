package com.todolist.backend.dto.item;

import lombok.*;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemGetDto extends ItemDto {

    private Integer id;

    private Boolean state;

    private Timestamp created;

    private Timestamp expired;

    private Integer todoListId;
}
