package com.todolist.backend.controller;

import com.todolist.backend.controller.util.ResponseEntityUtil;
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

        if (!userService.isUniqueData(requestUser.getUsername(), requestUser.getEmail())) {
            return ResponseEntityUtil.generateResponse(HttpStatus.CONFLICT,
                    "errorMessage", "There is already users with this data in the system");
        }

        User user = mapper.map(requestUser, User.class);
        user = userService.save(user);
        UserGetDto userGetDto = mapper.map(user, UserGetDto.class);

        return ResponseEntityUtil.generateResponse(HttpStatus.CREATED, "user", userGetDto);
    }

    @Secured({ROLE_ADMIN, ROLE_USER})
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(Principal principal, @PathVariable("id") Integer userId) {

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            return ResponseEntityUtil.generateResponse(HttpStatus.FORBIDDEN,
                    "errorMessage", "Login to see user information");
        }

        User user = userService.getById(userId);
        UserGetDto userDto = mapper.map(user, UserGetDto.class);

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, "user", userDto);
    }

    @Secured({ROLE_ADMIN, ROLE_USER})
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            Principal principal,
            @PathVariable("userId") Integer userId,
            @Valid @RequestBody UserUpdateDto requestUserDto) {

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            return ResponseEntityUtil.generateResponse(HttpStatus.FORBIDDEN,
                    "errorMessage", "Login to edit user information");
        }

        if (!userService.isUniqueDataForUpdate(userId, requestUserDto.getUsername())) {
            return ResponseEntityUtil.generateResponse(HttpStatus.CONFLICT,
                    "errorMessage", "There is already users with this username in the system");
        }

        User currentUser = userService.getById(userId);
        if (userService.areDifferentPasswords(currentUser.getPassword(), requestUserDto.getPassword())) {
            return ResponseEntityUtil.generateResponse(HttpStatus.BAD_REQUEST,
                    "errorMessage", "The current password is incorrect");
        }

        User user = userService.update(
                currentUser,
                requestUserDto.getFirstname(),
                requestUserDto.getLastname(),
                requestUserDto.getUsername(),
                requestUserDto.getPassword());
        UserGetDto userGetDto = mapper.map(user, UserGetDto.class);

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, "user", userGetDto);
    }


    @Secured({ROLE_ADMIN, ROLE_USER})
    @PutMapping("/{userId}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            Principal principal,
            @PathVariable("userId") Integer userId,
            @Valid @RequestBody UserUpdatePasswordDto requestPasswordDto
    ) {

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            return ResponseEntityUtil.generateResponse(HttpStatus.FORBIDDEN,
                    "errorMessage", "Login to edit user information");
        }

        User currentUser = userService.getById(userId);
        if (userService.areDifferentPasswords(currentUser.getPassword(), requestPasswordDto.getCurrentPassword())) {
            return ResponseEntityUtil.generateResponse(HttpStatus.BAD_REQUEST,
                    "errorMessage", "The current password is incorrect");
        }

        if (requestPasswordDto.getCurrentPassword().equals(requestPasswordDto.getNewPassword())) {
            return ResponseEntityUtil.generateResponse(HttpStatus.BAD_REQUEST,
                    "errorMessage", "The new password is the same as the current");
        }

        userService.updatePassword(currentUser, requestPasswordDto.getNewPassword());

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, null, null);
    }
}
