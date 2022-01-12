package com.todolist.backend.controller;

import com.todolist.backend.entity.Role;
import com.todolist.backend.entity.User;
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

import java.util.stream.Collectors;

import static com.todolist.backend.entity.Roles.Constants.*;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerIntegrationTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    UserService userService;

    @Autowired
    UserController userController;

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

        User user = new User();
        user.setFirstname("Juan");
        user.setLastname("Perez");
        user.setEmail("juan_perez@email.com");
        user.setUsername("juan.perez");
        user.setPassword("JuanPerez2022");
        userService.save(user);
    }

    @After
    public void tearDown() {
        userService.deleteAll();
        roleRepo.deleteAll();
    }

    // -----------------------------------------------------
    //             -- Tests for CREATE USER
    // -----------------------------------------------------
    @Test
    public void givenValidUserData_whenPostRequest_thenShouldResponseCreated() throws Exception {

        //given
        String user = "{" +
                "\"firstname\": \"Pablo\", " +
                "\"lastname\": \"Sanchez\", " +
                "\"email\": \"pablo_sanchez@email.com\", " +
                "\"username\": \"pablo.sanchez\", " +
                "\"password\": \"Pablo2022\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users")
                .content(user)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isCreated());
        String content = result.andReturn().getResponse().getContentAsString();
        assertTrue(content.contains(
                "\"firstname\":\"Pablo\"," +
                "\"lastname\":\"Sanchez\"," +
                "\"email\":\"pablo_sanchez@email.com\"," +
                "\"username\":\"pablo.sanchez\""));
    }

    @Test
    public void givenExistingUserData_whenPostRequest_thenShouldResponseConflict() throws Exception {
        //given
        String user = "{" +
                "\"firstname\": \"test\", " +
                "\"lastname\": \"user\", " +
                "\"email\": \"test_user@email.com\", " +
                "\"username\": \"test.user\", " +
                "\"password\": \"TestUser2022\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users")
                .content(user)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isConflict());
    }

    // -----------------------------------------------------
    //              -- Tests for GET USER
    // -----------------------------------------------------
    @Test
    public void givenValidUserIdAndValidToken_whenGetRequest_thenShouldResponseOk() throws Exception {
        //given
        String userId = Integer.toString(testUser.getId());

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
        String content = result.andReturn().getResponse().getContentAsString();
        assertTrue(content.contains(
                "\"firstname\":\"test\"," +
                "\"lastname\":\"user\"," +
                "\"email\":\"test_user@email.com\"," +
                "\"username\":\"test.user\""));
    }

    @Test
    public void givenValidUserIdWithoutToken_whenGetRequest_thenShouldResponseForbidden() throws Exception {
        //given
        String userId = Integer.toString(testUser.getId());

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenInvalidUserIdAndValidToken_whenGetRequest_thenShouldResponseForbidden() throws Exception {
        //given
        String userId = Integer.toString(testUser.getId() + 1);

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //          -- Tests for UPDATE USER DATA
    // -----------------------------------------------------
    @Test
    public void givenValidUserDataAndValidUserIdWithToken_whenPutRequest_thenShouldResponseOk() throws Exception {

        //given
        String testUserId = Integer.toString(testUser.getId());
        String testUserUpdateData = "{" +
                "\"firstname\": \"user\", " +
                "\"lastname\": \"test\", " +
                "\"username\": \"user.test\", " +
                "\"password\": \"TestUser2022\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + testUserId)
                .content(testUserUpdateData)
                .contentType(APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
        String content = result.andReturn().getResponse().getContentAsString();
        assertTrue(content.contains(
                "\"firstname\":\"user\"," +
                "\"lastname\":\"test\"," +
                "\"email\":\"test_user@email.com\"," +
                "\"username\":\"user.test\""));
    }

    @Test
    public void givenValidUserDataAndInvalidUserIdWithToken_whenPutRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String testUserId = Integer.toString(testUser.getId() + 1);
        String testUserUpdateData = "{" +
                "\"firstname\": \"user\", " +
                "\"lastname\": \"test\", " +
                "\"username\": \"user.test\", " +
                "\"password\": \"TestUser2022\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + testUserId)
                .content(testUserUpdateData)
                .contentType(APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenExistingUserDataAndValidUserIdWithToken_whenPutRequest_thenShouldResponseConflict() throws Exception {

        //given
        String testUserId = Integer.toString(testUser.getId());
        String testUserUpdateData = "{" +
                "\"firstname\": \"user\", " +
                "\"lastname\": \"test\", " +
                "\"username\": \"juan.perez\", " +
                "\"password\": \"TestUser2022\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + testUserId)
                .content(testUserUpdateData)
                .contentType(APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isConflict());
    }

    @Test
    public void givenInvalidUserPasswordAndValidDataAndUserIdWithToken_whenPutRequest_thenShouldResponseBadRequest() throws Exception {

        //given
        String testUserId = Integer.toString(testUser.getId());
        String testUserUpdateData = "{" +
                "\"firstname\": \"user\", " +
                "\"lastname\": \"test\", " +
                "\"username\": \"user.test\", " +
                "\"password\": \"UserTest2022\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + testUserId)
                .content(testUserUpdateData)
                .contentType(APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void givenValidUserDataAndValidUserIdWithoutToken_whenPutRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String testUserId = Integer.toString(testUser.getId());
        String testUserUpdateData = "{" +
                "\"firstname\": \"user\", " +
                "\"lastname\": \"test\", " +
                "\"username\": \"user.test\", " +
                "\"password\": \"TestUser2022\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + testUserId)
                .content(testUserUpdateData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //        -- Tests for UPDATE USER PASSWORD
    // -----------------------------------------------------
    @Test
    public void givenValidUserPasswordAndValidUserIdWithToken_whenPutRequest_thenShouldResponseOk() throws Exception {

        //given
        String testUserId = Integer.toString(testUser.getId());
        String testUserUpdatePassword = "{" +
                "\"currentPassword\": \"TestUser2022\", " +
                "\"newPassword\": \"TestUser2024\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + testUserId + "/password")
                .content(testUserUpdatePassword)
                .contentType(APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
    }

    @Test
    public void givenValidUserPasswordAndInvalidUserIdWithToken_whenPutRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String testUserId = Integer.toString(testUser.getId() + 1);
        String testUserUpdatePassword = "{" +
                "\"currentPassword\": \"TestUser2022\", " +
                "\"newPassword\": \"TestUser2024\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + testUserId + "/password")
                .content(testUserUpdatePassword)
                .contentType(APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenInvalidUserPasswordAndValidUserIdWithToken_whenPutRequest_thenShouldResponseBadRequest() throws Exception {

        //given
        String testUserId = Integer.toString(testUser.getId());
        String testUserUpdatePassword = "{" +
                "\"currentPassword\": \"TestUser2023\", " +
                "\"newPassword\": \"TestUser2024\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + testUserId + "/password")
                .content(testUserUpdatePassword)
                .contentType(APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void givenTwiceSameUserPasswordAndValidUserIdWithToken_whenPutRequest_thenShouldResponseBadRequest() throws Exception {

        //given
        String testUserId = Integer.toString(testUser.getId());
        String testUserUpdatePassword = "{" +
                "\"currentPassword\": \"TestUser2022\", " +
                "\"newPassword\": \"TestUser2022\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + testUserId + "/password")
                .content(testUserUpdatePassword)
                .contentType(APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void givenValidUserPasswordAndValidUserIdWithoutToken_whenPutRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String testUserId = Integer.toString(testUser.getId());
        String testUserUpdatePassword = "{" +
                "\"currentPassword\": \"TestUser2022\", " +
                "\"newPassword\": \"TestUser2024\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + testUserId + "/password")
                .content(testUserUpdatePassword)
                .contentType(APPLICATION_JSON_VALUE));

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
