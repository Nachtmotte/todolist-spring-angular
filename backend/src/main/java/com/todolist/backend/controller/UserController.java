package com.todolist.backend.controller;

import com.todolist.backend.dto.user.*;
import com.todolist.backend.entity.User;
import com.todolist.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    private final ModelMapper mapper;

    @Secured("permitAll")
    @PostMapping()
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserPostDto requestUser) {
        Map<String, Object> bodyResponse = new HashMap<>();

        if (!userService.verifyUniqueData(requestUser.getUsername(), requestUser.getEmail())) {
            bodyResponse.put("errorMessage", "There is already users with this data in the system");
            return new ResponseEntity<>(bodyResponse, HttpStatus.CONFLICT);
        }

        User user = mapUserPostDtoToUserEntity(requestUser);
        user = userService.save(user);
        UserGetDto userGetDto = mapUserEntityToUserGetDto(user);

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

        UserGetDto userDto = mapUserEntityToUserGetDto(user);
        bodyResponse.put("user", userDto);
        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }

    public User mapUserPostDtoToUserEntity(UserPostDto userPostDto){
        return mapper.map(userPostDto, User.class);
    }

    public UserGetDto mapUserEntityToUserGetDto(User user){
        return mapper.map(user, UserGetDto.class);
    }
}
