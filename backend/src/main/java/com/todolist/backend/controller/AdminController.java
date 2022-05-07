package com.todolist.backend.controller;

import com.todolist.backend.controller.util.ResponseEntityUtil;
import com.todolist.backend.dto.role.RoleGetDto;
import com.todolist.backend.dto.user.PageUserDto;
import com.todolist.backend.dto.user.UserGetWithRolesDto;
import com.todolist.backend.entity.*;
import com.todolist.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

    private final RoleService roleService;

    private final UserService userService;

    private final ModelMapper mapper;

    @PostMapping("users/{userId}/roles/{roleId}")
    public ResponseEntity<Map<String, Object>> addRoleToUser(
            @PathVariable("userId") Integer userId,
            @PathVariable("roleId") Integer roleId) {

        User user = userService.getById(userId);
        if (user == null) {
            return ResponseEntityUtil.generateResponse(HttpStatus.NOT_FOUND,
                    "errorMessage", "The user does not exist");
        }

        Role role = roleService.getById(roleId);
        if (role == null) {
            return ResponseEntityUtil.generateResponse(HttpStatus.NOT_FOUND,
                    "errorMessage", "The role does not exist");
        }

        if (user.getRoles().stream().anyMatch(r -> r.getId() == role.getId())) {
            return ResponseEntityUtil.generateResponse(HttpStatus.CONFLICT,
                    "errorMessage", "The user already has that role");
        }

        user = roleService.addRoleToUser(user, role);
        UserGetWithRolesDto userGetDto = mapper.map(user, UserGetWithRolesDto.class);

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, "user", userGetDto);
    }

    @DeleteMapping("users/{userId}/roles/{roleId}")
    public ResponseEntity<Map<String, Object>> removeRoleFromUser(
            @PathVariable("userId") Integer userId,
            @PathVariable("roleId") Integer roleId) {

        User user = userService.getById(userId);
        if (user == null) {
            return ResponseEntityUtil.generateResponse(HttpStatus.NOT_FOUND,
                    "errorMessage", "The user does not exist");
        }

        if (user.getRoles().stream().noneMatch(r -> r.getId() == roleId)) {
            return ResponseEntityUtil.generateResponse(HttpStatus.CONFLICT,
                    "errorMessage", "The user no longer has that role.");
        }

        user = roleService.removeRoleFromUser(user, roleId);
        UserGetWithRolesDto userGetDto = mapper.map(user, UserGetWithRolesDto.class);

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, "user", userGetDto);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(value = "page", required = false) Integer pageNumber,
            @RequestParam(value = "per_page", required = false) Integer users_per_page,
            @RequestParam(value = "orderby", required = false) String order,
            @RequestParam(value = "direction", required = false) String direction) {

        pageNumber = pageNumber == null ? 0 : pageNumber;
        users_per_page = users_per_page == null ? 10 : users_per_page;
        order = order == null ? "id" : order;
        direction = direction == null ? "asc" : direction;

        Pageable pageable = PageRequest
                .of(pageNumber,
                        users_per_page,
                        Sort.by(direction.equals("desc") ? DESC : ASC, order));
        Page<User> users = userService.getAll(pageable);
        PageUserDto pageUserDto = mapper.map(users, PageUserDto.class);

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, "users", pageUserDto);
    }

    @GetMapping("/roles")
    public ResponseEntity<Map<String, Object>> getRoles() {
        List<Role> roles = roleService.getAll();
        List<RoleGetDto> rolesDto = mapper.map(roles, new TypeToken<List<RoleGetDto>>() {
        }.getType());

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, "roles", rolesDto);
    }
}
