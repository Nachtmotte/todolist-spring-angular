package com.todolist.backend.controller;

import com.todolist.backend.controller.util.ModelMapperService;
import com.todolist.backend.dto.picture.ProfilePictureDto;
import com.todolist.backend.entity.*;
import com.todolist.backend.service.*;
import lombok.RequiredArgsConstructor;
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
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfilePictureController {

    private final UserService userService;

    private final ProfilePictureService profilePictureService;

    private final ModelMapperService mapperService;

    @PostMapping("/{userId}/profile-picture")
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

        ProfilePicture profilePicture = mapperService.mapProfilePictureDtoToProfilePictureEntity(requestPicture);
        profilePicture = profilePictureService.save(currentUser, profilePicture);
        ProfilePictureDto profilePictureDto = mapperService.mapProfilePictureEntityToProfilePictureDto(profilePicture);

        bodyResponse.put("profilePicture", profilePictureDto);
        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/profile-picture")
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

        profilePictureService.delete(currentUser.getProfilePicture().getId());

        return new ResponseEntity<>(bodyResponse, HttpStatus.OK);
    }
}
