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
import java.security.Principal;
import java.util.*;

import static com.todolist.backend.entity.Roles.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    private final ModelMapper mapper;

    @PostMapping()
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserPostDto requestUser) {
        Map<String, Object> bodyResponse = new HashMap<>();

        if (!userService.isUniqueData(requestUser.getUsername(), requestUser.getEmail())) {
            bodyResponse.put("errorMessage", "There is already users with this data in the system");
            return new ResponseEntity<>(bodyResponse, HttpStatus.CONFLICT);
        }

        User user = mapper.map(requestUser, User.class);
        user = userService.save(user);
        UserGetDto userGetDto = mapper.map(user, UserGetDto.class);

        bodyResponse.put("user", userGetDto);
        return new ResponseEntity<>(bodyResponse, HttpStatus.CREATED);
    }

    @Secured({ROLE_ADMIN, ROLE_USER})
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(Principal principal, @PathVariable("id") Integer userId) {
        Map<String, Object> bodyResponse = new HashMap<>();

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            bodyResponse.put("errorMessage", "Login to see user information");
            return new ResponseEntity<>(bodyResponse, HttpStatus.FORBIDDEN);
        }

        User user = userService.getById(userId);
        UserGetDto userDto = mapper.map(user, UserGetDto.class);

        bodyResponse.put("user", userDto);
        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }

    @Secured({ROLE_ADMIN, ROLE_USER})
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            Principal principal,
            @PathVariable("userId") Integer userId,
            @Valid @RequestBody UserUpdateDto requestUserDto) {

        Map<String, Object> bodyResponse = new HashMap<>();

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            bodyResponse.put("errorMessage", "Login to edit user information");
            return new ResponseEntity<>(bodyResponse, HttpStatus.FORBIDDEN);
        }

        if (!userService.isUniqueDataForUpdate(userId, requestUserDto.getUsername())) {
            bodyResponse.put("errorMessage", "There is already users with this username in the system");
            return new ResponseEntity<>(bodyResponse, HttpStatus.CONFLICT);
        }

        User currentUser = userService.getById(userId);
        if (userService.areDifferentPasswords(currentUser.getPassword(), requestUserDto.getPassword())) {
            bodyResponse.put("errorMessage", "The current password is incorrect");
            return new ResponseEntity<>(bodyResponse, HttpStatus.BAD_REQUEST);
        }

        User user = userService.update(
                currentUser,
                requestUserDto.getFirstname(),
                requestUserDto.getLastname(),
                requestUserDto.getUsername(),
                requestUserDto.getPassword());
        UserGetDto userGetDto = mapper.map(user, UserGetDto.class);

        bodyResponse.put("user", userGetDto);
        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }


    @Secured({ROLE_ADMIN, ROLE_USER})
    @PutMapping("/{userId}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            Principal principal,
            @PathVariable("userId") Integer userId,
            @Valid @RequestBody UserUpdatePasswordDto requestPasswordDto
    ) {

        Map<String, Object> bodyResponse = new HashMap<>();

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            bodyResponse.put("errorMessage", "Login to edit user information");
            return new ResponseEntity<>(bodyResponse, HttpStatus.FORBIDDEN);
        }

        User currentUser = userService.getById(userId);
        if (userService.areDifferentPasswords(currentUser.getPassword(), requestPasswordDto.getCurrentPassword())) {
            bodyResponse.put("errorMessage", "The current password is incorrect");
            return new ResponseEntity<>(bodyResponse, HttpStatus.BAD_REQUEST);
        }

        if (requestPasswordDto.getCurrentPassword().equals(requestPasswordDto.getNewPassword())) {
            bodyResponse.put("errorMessage", "The new password is the same as the current");
            return new ResponseEntity<>(bodyResponse, HttpStatus.BAD_REQUEST);
        }

        userService.updatePassword(currentUser, requestPasswordDto.getNewPassword());

        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }
}
