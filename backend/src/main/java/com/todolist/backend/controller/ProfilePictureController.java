package com.todolist.backend.controller;

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

        Map<String, Object> bodyResponse = new HashMap<>();

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            bodyResponse.put("errorMessage", "Login to add profile picture to user");
            return new ResponseEntity<>(bodyResponse, HttpStatus.FORBIDDEN);
        }

        User currentUser = userService.getById(userId);
        if (currentUser.getProfilePicture() != null){
            bodyResponse.put("errorMessage", "The user already have a profile picture");
            return new ResponseEntity<>(bodyResponse, HttpStatus.CONFLICT);
        }

        ProfilePicture profilePicture = mapper.map(requestPicture, ProfilePicture.class);
        profilePicture = profilePictureService.save(currentUser, profilePicture);
        ProfilePictureDto profilePictureDto = mapper.map(profilePicture, ProfilePictureDto.class);

        bodyResponse.put("profilePicture", profilePictureDto);
        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<Map<String, Object>> addProfilePicture(
            Principal principal,
            @PathVariable("userId") Integer userId) {

        Map<String, Object> bodyResponse = new HashMap<>();

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            bodyResponse.put("errorMessage", "Login to delete profile picture to user");
            return new ResponseEntity<>(bodyResponse, HttpStatus.FORBIDDEN);
        }

        User currentUser = userService.getById(userId);
        if (currentUser.getProfilePicture() == null){
            bodyResponse.put("errorMessage", "The user don't have a profile picture");
            return new ResponseEntity<>(bodyResponse, HttpStatus.CONFLICT);
        }

        profilePictureService.delete(currentUser.getProfilePicture());

        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }
}
