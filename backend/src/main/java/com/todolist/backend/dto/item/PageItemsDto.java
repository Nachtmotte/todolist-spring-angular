package com.todolist.backend.dto.item;

import lombok.Data;

import java.util.List;

@Data
public class PageItemsDto {

    private boolean first;

    private boolean last;

    private int number;

    private int numberOfElements;

    private int size;

    private int totalPages;

    private long totalElements;

    private List<ItemGetDto> content;
}
