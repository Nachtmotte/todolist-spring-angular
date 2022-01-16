package com.todolist.backend.controller;

import com.todolist.backend.controller.util.ResponseEntityUtil;
import com.todolist.backend.dto.picture.ProfilePictureDto;
import com.todolist.backend.entity.*;
import com.todolist.backend.service.*;
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
@Secured({ROLE_ADMIN, ROLE_USER})
@RequestMapping(path = "/users/{userId}/profile-picture", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfilePictureController {

    private final UserService userService;

    private final ProfilePictureService profilePictureService;

    private final ModelMapper mapper;

    @PostMapping()
    public ResponseEntity<Map<String, Object>> addProfilePicture(
            Principal principal,
            @PathVariable("userId") Integer userId,
            @Valid @RequestBody ProfilePictureDto requestPicture) {

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            return ResponseEntityUtil.generateResponse(HttpStatus.FORBIDDEN,
                    "errorMessage", "Login to add profile picture to user");
        }

        User currentUser = userService.getById(userId);
        if (currentUser.getProfilePicture() != null){
            return ResponseEntityUtil.generateResponse(HttpStatus.CONFLICT,
                    "errorMessage", "The user already have a profile picture");
        }

        ProfilePicture profilePicture = mapper.map(requestPicture, ProfilePicture.class);
        profilePicture = profilePictureService.save(currentUser, profilePicture);
        ProfilePictureDto profilePictureDto = mapper.map(profilePicture, ProfilePictureDto.class);

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, "profilePicture", profilePictureDto);
    }

    @DeleteMapping()
    public ResponseEntity<Map<String, Object>> addProfilePicture(
            Principal principal,
            @PathVariable("userId") Integer userId) {

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            return ResponseEntityUtil.generateResponse(HttpStatus.FORBIDDEN,
                    "errorMessage", "Login to delete profile picture to user");
        }

        User currentUser = userService.getById(userId);
        if (currentUser.getProfilePicture() == null){
            return ResponseEntityUtil.generateResponse(HttpStatus.CONFLICT,
                    "errorMessage", "The user don't have a profile picture");
        }

        profilePictureService.delete(currentUser.getProfilePicture());

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, null, null);
    }
}
