package com.todolist.backend.controller;

import com.todolist.backend.entity.Role;
import com.todolist.backend.entity.User;
import com.todolist.backend.security.util.JwtService;
import com.todolist.backend.service.RoleService;
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

import static com.todolist.backend.entity.Roles.Constants.ROLE_ADMIN;
import static com.todolist.backend.entity.Roles.Constants.ROLE_USER;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminControllerIntegrationTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;

    @Autowired
    AdminController adminController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private User adminUser;

    private User testUser;

    private Role roleUser;

    private Role roleAdmin;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        roleUser = new Role();
        roleUser.setName(ROLE_USER);
        roleUser = roleService.save(roleUser);
        roleAdmin = new Role();
        roleAdmin.setName(ROLE_ADMIN);
        roleAdmin = roleService.save(roleAdmin);

        adminUser = new User();
        adminUser.setFirstname("admin");
        adminUser.setLastname("user");
        adminUser.setEmail("admin_user@email.com");
        adminUser.setUsername("admin.user");
        adminUser.setPassword("AdminUser2022");
        adminUser = userService.save(adminUser);
        roleService.addRoleToUser(adminUser, roleAdmin);

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
        userService.deleteAll();
        roleService.deleteAll();
    }

    // -----------------------------------------------------
    //           -- Tests for ADD ROLE TO USER
    // -----------------------------------------------------
    @Test
    public void givenValidUserIdAndValidRoleIdWithValidToken_whenPostRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String roleId = Integer.toString(roleAdmin.getId());

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/roles/" + roleId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.user.roles", hasSize(2)));
    }

    @Test
    public void givenInvalidUserIdAndValidRoleIdWithValidToken_whenPostRequest_thenShouldResponseNotFound() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId() + 1);
        String roleId = Integer.toString(roleAdmin.getId());

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/roles/" + roleId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void givenValidUserIdAndInvalidRoleIdWithValidToken_whenPostRequest_thenShouldResponseNotFound() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String roleId = Integer.toString(roleAdmin.getId() + 1);

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/roles/" + roleId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void givenValidUserIdAndRoleIdThatAlreadyHaveWithValidToken_whenPostRequest_thenShouldResponseConflict() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String roleId = Integer.toString(roleUser.getId());

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/roles/" + roleId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isConflict());
    }

    @Test
    public void givenValidUserIdAndValidRoleIdWithInvalidToken_whenPostRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String roleId = Integer.toString(roleAdmin.getId());
        adminUser = roleService.removeRoleFromUser(adminUser, roleAdmin.getId());

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/roles/" + roleId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //           -- Tests for ADD ROLE TO USER
    // -----------------------------------------------------
    @Test
    public void givenValidUserIdAndValidRoleIdWithValidToken_whenDeleteRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String roleId = Integer.toString(roleUser.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/roles/" + roleId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.user.roles", hasSize(0)));
    }

    @Test
    public void givenInvalidUserIdAndValidRoleIdWithValidToken_whenDeleteRequest_thenShouldResponseNotFound() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId() + 1);
        String roleId = Integer.toString(roleUser.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/roles/" + roleId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void givenValidUserIdAndRoleIdThatDontHaveWithValidToken_whenDeleteRequest_thenShouldResponseConflict() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String roleId = Integer.toString(roleAdmin.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/roles/" + roleId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isConflict());
    }

    @Test
    public void givenValidUserIdAndValidRoleIdWithInvalidToken_whenDeleteRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String roleId = Integer.toString(roleUser.getId());
        adminUser = roleService.removeRoleFromUser(adminUser, roleAdmin.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/roles/" + roleId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //           -- Tests for GET ALL USERS
    // -----------------------------------------------------
    @Test
    public void givenValidToken_whenGetRequestToUsers_thenShouldResponseOk() throws Exception{

        //given
        String accessToken = generateToken();

        //when
        ResultActions result = mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + accessToken));

        //then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.users.content", hasSize(2)));
    }

    @Test
    public void givenInvalidToken_whenGetRequestToUsers_thenShouldResponseForbidden() throws Exception{

        //given
        adminUser = roleService.removeRoleFromUser(adminUser, roleAdmin.getId());
        String accessToken = generateToken();

        //when
        ResultActions result = mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + accessToken));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void whenGetRequestToUsersWithoutToken_thenShouldResponseForbidden() throws Exception{

        //when
        ResultActions result = mockMvc.perform(get("/users"));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //           -- Tests for GET ALL ROLES
    // -----------------------------------------------------
    @Test
    public void givenValidToken_whenGetRequestToRoles_thenShouldResponseOk() throws Exception{

        //given
        String accessToken = generateToken();

        //when
        ResultActions result = mockMvc.perform(get("/roles")
                .header("Authorization", "Bearer " + accessToken));

        //then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.roles", hasSize(2)));
    }

    @Test
    public void givenInvalidToken_whenGetRequestToRoles_thenShouldResponseForbidden() throws Exception{

        //given
        adminUser = roleService.removeRoleFromUser(adminUser, roleAdmin.getId());
        String accessToken = generateToken();

        //when
        ResultActions result = mockMvc.perform(get("/roles")
                .header("Authorization", "Bearer " + accessToken));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void whenGetRequestToUsersWithoutRoles_thenShouldResponseForbidden() throws Exception{

        //when
        ResultActions result = mockMvc.perform(get("/roles"));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //                 -- Private Method
    // -----------------------------------------------------
    private String generateToken() {
        return jwtService.createAccessToken(Integer.toString(adminUser.getId()),
                adminUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
    }
}