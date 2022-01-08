package com.todolist.backend.controller;

import com.todolist.backend.controller.util.ModelMapperService;
import com.todolist.backend.dto.user.*;
import com.todolist.backend.entity.User;
import com.todolist.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static com.todolist.backend.entity.Roles.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    private final ModelMapperService mapperService;

    @PostMapping()
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserPostDto requestUser) {
        Map<String, Object> bodyResponse = new HashMap<>();

        if (!userService.verifyUniqueData(requestUser.getUsername(), requestUser.getEmail())) {
            bodyResponse.put("errorMessage", "There is already users with this data in the system");
            return new ResponseEntity<>(bodyResponse, HttpStatus.CONFLICT);
        }

        User user = mapperService.mapUserPostDtoToUserEntity(requestUser);
        user = userService.save(user);
        UserGetDto userGetDto = mapperService.mapUserEntityToUserGetDto(user);

        bodyResponse.put("user", userGetDto);

        return new ResponseEntity<>(bodyResponse, HttpStatus.CREATED);
    }

    @Secured({ROLE_ADMIN, ROLE_USER})
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable("id") Integer id) {
        Map<String, Object> bodyResponse = new HashMap<>();
        User user = userService.getById(id);

        if (user == null) {
            bodyResponse.put("errorMessage", "There is no such user in the system");
            return new ResponseEntity<>(bodyResponse, HttpStatus.NOT_FOUND);
        }

        UserGetDto userDto = mapperService.mapUserEntityToUserGetDto(user);
        bodyResponse.put("user", userDto);
        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }
}
