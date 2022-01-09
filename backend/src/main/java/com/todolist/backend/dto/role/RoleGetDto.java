package com.todolist.backend.dto.role;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleGetDto extends RoleDto{

    private Integer id;
}
