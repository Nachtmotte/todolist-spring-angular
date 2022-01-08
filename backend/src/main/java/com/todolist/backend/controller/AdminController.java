package com.todolist.backend.controller;

import com.todolist.backend.controller.util.ModelMapperService;
import com.todolist.backend.dto.role.RoleDto;
import com.todolist.backend.dto.user.UserGetDto;
import com.todolist.backend.entity.*;
import com.todolist.backend.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.todolist.backend.entity.Roles.Constants.ROLE_ADMIN;

@Slf4j
@RestController
@Secured(ROLE_ADMIN)
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private final RoleService roleService;

    private final UserService userService;

    private final ModelMapperService mapperService;

    @PostMapping("users/{userId}/roles/{roleId}")
    public ResponseEntity<Map<String, Object>> addRoleToUser(@PathVariable("userId")Integer userId, @PathVariable("roleId")Integer roleId) {
        Map<String, Object> bodyResponse = new HashMap<>();

        User user = userService.getById(userId);
        if (user == null) {
            bodyResponse.put("errorMessage", "The user does not exist");
            return new ResponseEntity<>(bodyResponse, HttpStatus.NOT_FOUND);
        }

        Role role = roleService.getById(roleId);
        if (role == null) {
            bodyResponse.put("errorMessage", "The role does not exist");
            return new ResponseEntity<>(bodyResponse, HttpStatus.NOT_FOUND);
        }

        if (user.getRoles().stream().anyMatch(r -> r.getId() == role.getId())) {
            bodyResponse.put("errorMessage", "The user already has that role");
            return new ResponseEntity<>(bodyResponse, HttpStatus.CONFLICT);
        }

        user = roleService.addRoleToUser(user, role);
        UserGetDto userGetDto = mapperService.mapUserEntityToUserGetDto(user);

        bodyResponse.put("user", userGetDto);

        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }

    @DeleteMapping("users/{userId}/roles/{roleId}")
    public ResponseEntity<Map<String, Object>> removeRoleFromUser(@PathVariable("userId")Integer userId, @PathVariable("roleId")Integer roleId) {
        Map<String, Object> bodyResponse = new HashMap<>();

        User user = userService.getById(userId);
        if (user == null) {
            bodyResponse.put("errorMessage", "The user does not exist");
            return new ResponseEntity<>(bodyResponse, HttpStatus.NOT_FOUND);
        }

        if (user.getRoles().stream().noneMatch(r -> r.getId() == roleId)) {
            bodyResponse.put("errorMessage", "The user no longer has that role.");
            return new ResponseEntity<>(bodyResponse, HttpStatus.CONFLICT);
        }

        user = roleService.removeRoleFromUser(user, roleId);
        UserGetDto userGetDto = mapperService.mapUserEntityToUserGetDto(user);

        bodyResponse.put("user", userGetDto);

        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(){
        Map<String, Object> bodyResponse = new HashMap<>();
        List<User> users = userService.getAll();
        List<UserGetDto> userGetDtos = mapperService.mapUserEntitiesToUserDtos(users);
        bodyResponse.put("users", userGetDtos);
        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<Map<String, Object>> getRoles(){
        Map<String, Object> bodyResponse = new HashMap<>();
        List<Role> roles = roleService.getAll();
        List<RoleDto> rolesDto = mapperService.mapRoleEntitiesToRoleDtos(roles);
        bodyResponse.put("roles", rolesDto);
        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }
}
