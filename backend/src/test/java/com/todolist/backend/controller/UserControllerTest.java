package com.todolist.backend.controller;

import com.todolist.backend.dto.user.UserGetDto;
import com.todolist.backend.dto.user.UserPostDto;
import com.todolist.backend.entity.ProfilePicture;
import com.todolist.backend.entity.User;
import com.todolist.backend.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController underTest;

    @BeforeEach
    void setUp() {
        ModelMapper mapper = new ModelMapper();
        underTest = new UserController(userService, mapper);
    }

    @Test
    @Disabled
    void createUser() {
    }

    @Test
    @Disabled
    void getUserByUsername() {
    }

    @Test
    void whenMapUserPostDtoToUserEntity_thenCorrect() {
        //given
        UserPostDto userPostDto = new UserPostDto();
        userPostDto.setUsername("Juan");
        userPostDto.setLastname("Perez");
        userPostDto.setEmail("juan_perez@email.com");
        userPostDto.setUsername("juan.perez");
        userPostDto.setEmail("Juan2022");

        //when
        User user = underTest.mapUserPostDtoToUserEntity(userPostDto);

        //then
        assertEquals(userPostDto.getUsername(), user.getUsername());
        assertEquals(userPostDto.getLastname(), user.getLastname());
        assertEquals(userPostDto.getEmail(), user.getEmail());
        assertEquals(userPostDto.getUsername(), user.getUsername());
        assertEquals(userPostDto.getPassword(), user.getPassword());
    }

    @Test
    @Disabled
    void whenMapUserEntityToUserGetDto_thenCorrect() {
        //given
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setId(1);
        profilePicture.setName("imagen");
        profilePicture.setUrl("https://www.storage.com/imagen.jpg");

        User user = new User();
        user.setUsername("Juan");
        user.setLastname("Perez");
        user.setEmail("juan_perez@email.com");
        user.setUsername("juan.perez");
        user.setCreated(Timestamp.from(Instant.now()));
        user.setVerified(false);

        profilePicture.setUser(user);
        user.setProfilePicture(profilePicture);

        //when
        UserGetDto userGetDto = underTest.mapUserEntityToUserGetDto(user);

        //then
        assertEquals(user.getUsername(), userGetDto.getUsername());
        assertEquals(user.getLastname(), userGetDto.getLastname());
        assertEquals(user.getEmail(), userGetDto.getEmail());
        assertEquals(user.getUsername(), userGetDto.getUsername());
        assertEquals(user.getCreated(), userGetDto.getCreated());
        assertEquals(user.isVerified(), userGetDto.getVerified());
        assertNotNull(userGetDto.getProfilePicture());
        assertEquals(user.getProfilePicture().getName(), userGetDto.getProfilePicture().getName());
        assertEquals(user.getProfilePicture().getUrl(), userGetDto.getProfilePicture().getUrl());
    }
}