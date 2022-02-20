package com.todolist.backend.dto.item;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemPutDto extends ItemDto {

    @NotNull(message = "The State of the Item is required.")
    private Boolean state;
}
