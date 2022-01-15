package com.todolist.backend.controller;

import com.todolist.backend.dto.role.RoleGetDto;
import com.todolist.backend.dto.user.PageUserDto;
import com.todolist.backend.dto.user.UserGetWithRolesDto;
import com.todolist.backend.entity.*;
import com.todolist.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.todolist.backend.entity.Roles.Constants.ROLE_ADMIN;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@Secured(ROLE_ADMIN)
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    @Value("${pagination.users.number}")
    int users_per_page;

    private final RoleService roleService;

    private final UserService userService;

    private final ModelMapper mapper;

    @PostMapping("users/{userId}/roles/{roleId}")
    public ResponseEntity<Map<String, Object>> addRoleToUser(
            @PathVariable("userId") Integer userId,
            @PathVariable("roleId") Integer roleId) {
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
        UserGetWithRolesDto userGetDto = mapper.map(user, UserGetWithRolesDto.class);

        bodyResponse.put("user", userGetDto);

        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }

    @DeleteMapping("users/{userId}/roles/{roleId}")
    public ResponseEntity<Map<String, Object>> removeRoleFromUser(
            @PathVariable("userId") Integer userId,
            @PathVariable("roleId") Integer roleId) {

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
        UserGetWithRolesDto userGetDto = mapper.map(user, UserGetWithRolesDto.class);

        bodyResponse.put("user", userGetDto);

        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(value = "page", required = false) Integer pageNumber,
            @RequestParam(value = "orderby", required = false) String order,
            @RequestParam(value = "direction", required = false) String direction) {

        pageNumber = pageNumber == null ? 0 : pageNumber;
        order = order == null ? "id" : order;
        direction = direction == null ? "asc" : direction;

        Map<String, Object> bodyResponse = new HashMap<>();
        Pageable pageable = PageRequest
                .of(pageNumber,
                        users_per_page,
                        Sort.by(direction.equals("desc") ? DESC : ASC, order));
        Page<User> users = userService.getAll(pageable);
        PageUserDto pageUserDto = mapper.map(users, PageUserDto.class);

        bodyResponse.put("users", pageUserDto);
        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<Map<String, Object>> getRoles() {
        Map<String, Object> bodyResponse = new HashMap<>();

        List<Role> roles = roleService.getAll();
        List<RoleGetDto> rolesDto = mapper.map(roles, new TypeToken<List<RoleGetDto>>() {
        }.getType());

        bodyResponse.put("roles", rolesDto);
        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }
}
