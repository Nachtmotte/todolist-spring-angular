package com.todolist.backend.dto.user;

import com.todolist.backend.dto.role.RoleDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserGetWithRolesDto extends UserGetDto{

    private Set<RoleDto> roles;
}
