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

import static org.hamcrest.Matchers.*;
import static com.todolist.backend.entity.Roles.Constants.ROLE_USER;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TokenControllerIntegrationTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    UserService userService;

    @Autowired
    TokenController tokenController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private User testUser;

    @Before
    public void setUp() {
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
        userService.deleteAll();
        roleRepo.deleteAll();
    }

    // -----------------------------------------------------
    //        -- Tests for GET REFRESH TOKEN
    // -----------------------------------------------------
    @Test
    public void givenValidRefreshToken_whenGetRequest_thenShouldResponseOk() throws Exception {

        //given
        String refreshToken = generateRefreshToken();

        //when
        ResultActions result = mockMvc.perform(get("/token/refresh")
                .header("Authorization", "Bearer " + refreshToken));

        //then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.accessToken", is(not(empty()))));
        result.andExpect(jsonPath("$.refreshToken", is(not(empty()))));
    }

    @Test
    public void givenInvalidRefreshToken_whenGetRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String refreshToken = generateRefreshToken().replace('.','_');

        //when
        ResultActions result = mockMvc.perform(get("/token/refresh")
                .header("Authorization", "Bearer " + refreshToken));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void whenGetRequestWithoutRefreshToken_thenShouldResponseForbidden() throws Exception {

        //when
        ResultActions result = mockMvc.perform(get("/token/refresh"));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //                 -- Private Method
    // -----------------------------------------------------
    private String generateRefreshToken() {
        return jwtService.createRefreshToken(Integer.toString(testUser.getId()));
    }
}