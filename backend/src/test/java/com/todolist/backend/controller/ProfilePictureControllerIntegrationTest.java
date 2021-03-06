package com.todolist.backend.controller;

import com.todolist.backend.entity.ProfilePicture;
import com.todolist.backend.entity.Role;
import com.todolist.backend.entity.User;
import com.todolist.backend.repository.ProfilePictureRepository;
import com.todolist.backend.repository.RoleRepository;
import com.todolist.backend.security.util.JwtService;
import com.todolist.backend.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.stream.Collectors;

import static com.todolist.backend.entity.Roles.Constants.ROLE_USER;
import static org.junit.Assert.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfilePictureControllerIntegrationTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    ProfilePictureRepository profilePictureRepo;

    @Autowired
    UserService userService;

    @Autowired
    ProfilePictureController profilePictureController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private User testUser;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        Role roleUser = new Role();
        roleUser.setName(ROLE_USER);
        roleRepo.save(roleUser);

        testUser = new User();
        testUser.setFirstname("test");
        testUser.setLastname("user");
        testUser.setEmail("test_user@email.com");
        testUser.setUsername("test.user");
        testUser.setPassword("TestUser2022");
        testUser = userService.save(testUser);
    }

    @After
    public void tearDown() {
        profilePictureRepo.deleteAll();
        userService.deleteAll();
        roleRepo.deleteAll();
    }

    // -----------------------------------------------------
    //      -- Tests for ADD PROFILE PICTURE TO USER
    // -----------------------------------------------------
    @Test
    public void givenValidUserIdAndProfilePictureDataThatDontHaveWithToken_whenPostRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String profilePictureName = "test.user" + Instant.now().toEpochMilli();
        String profilePictureUrl = "https://firebasestorage.googleapis.com/" + profilePictureName + ".jpg";
        String profilePictureData = "{" +
                "\"name\": \"" + profilePictureName + "\", " +
                "\"url\": \"" + profilePictureUrl + "\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/profile-picture")
                .header("Authorization", "Bearer " + generateToken())
                .content(profilePictureData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.profilePicture.name", is(profilePictureName)));
        result.andExpect(jsonPath("$.profilePicture.url", is(profilePictureUrl)));
        User user = userService.getById(testUser.getId());
        assertEquals(user.getProfilePicture().getName(), profilePictureName);
        assertEquals(user.getProfilePicture().getUrl(), profilePictureUrl);
    }

    @Test
    public void givenInvalidUserIdAndProfilePictureDataThatDontHaveWithToken_whenPostRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId() + 1);
        String profilePictureName = "test.user" + Instant.now().toEpochMilli();
        String profilePictureUrl = "https://firebasestorage.googleapis.com/" + profilePictureName + ".jpg";
        String profilePictureData = "{" +
                "\"name\": \"" + profilePictureName + "\", " +
                "\"url\": \"" + profilePictureUrl + "\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/profile-picture")
                .header("Authorization", "Bearer " + generateToken())
                .content(profilePictureData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenValidUserIdAndProfilePictureDataThatAlreadyHaveWithToken_whenPostRequest_thenShouldResponseConflict() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String profilePictureName = "test.user" + Instant.now().toEpochMilli();
        String profilePictureUrl = "https://firebasestorage.googleapis.com/" + profilePictureName + ".jpg";
        String profilePictureData = "{" +
                "\"name\": \"" + profilePictureName + "\", " +
                "\"url\": \"" + profilePictureUrl + "\"}";

        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setName(profilePictureName);
        profilePicture.setUrl(profilePictureUrl);
        profilePicture.setUser(testUser);
        profilePictureRepo.save(profilePicture);

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/profile-picture")
                .header("Authorization", "Bearer " + generateToken())
                .content(profilePictureData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isConflict());
    }

    @Test
    public void givenValidUserIdAndProfilePictureDataThatDontHaveWithoutToken_whenPostRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String profilePictureName = "test.user" + Instant.now().toEpochMilli();
        String profilePictureUrl = "https://firebasestorage.googleapis.com/" + profilePictureName + ".jpg";
        String profilePictureData = "{" +
                "\"name\": \"" + profilePictureName + "\", " +
                "\"url\": \"" + profilePictureUrl + "\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/profile-picture")
                .content(profilePictureData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //    -- Tests for DELETE PROFILE PICTURE TO USER
    // -----------------------------------------------------
    @Test
    public void givenValidUserIdThatAlreadyHaveProfilePictureWithToken_whenDeleteRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String profilePictureName = "test.user" + Instant.now().toEpochMilli();
        String profilePictureUrl = "https://firebasestorage.googleapis.com/" + profilePictureName + ".jpg";

        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setName(profilePictureName);
        profilePicture.setUrl(profilePictureUrl);
        profilePicture.setUser(testUser);
        profilePictureRepo.save(profilePicture);

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/profile-picture")
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
        User user = userService.getById(testUser.getId());
        assertNull(user.getProfilePicture());
    }

    @Test
    public void givenInvalidUserIdThatAlreadyHaveProfilePictureWithToken_whenDeleteRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId() + 1);

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/profile-picture")
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenValidUserIdThatDontHaveProfilePictureWithToken_whenDeleteRequest_thenShouldResponseConflict() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/profile-picture")
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isConflict());
    }

    @Test
    public void givenValidUserIdThatAlreadyHaveProfilePictureWithoutToken_whenDeleteRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String profilePictureName = "test.user" + Instant.now().toEpochMilli();
        String profilePictureUrl = "https://firebasestorage.googleapis.com/" + profilePictureName + ".jpg";

        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setName(profilePictureName);
        profilePicture.setUrl(profilePictureUrl);
        profilePicture.setUser(testUser);
        profilePictureRepo.save(profilePicture);

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/profile-picture"));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //                 -- Private Method
    // -----------------------------------------------------
    private String generateToken() {
        return jwtService.createAccessToken(Integer.toString(testUser.getId()),
                testUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
    }
}