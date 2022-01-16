package com.todolist.backend.controller;

import com.todolist.backend.entity.Role;
import com.todolist.backend.entity.TodoList;
import com.todolist.backend.entity.User;
import com.todolist.backend.repository.RoleRepository;
import com.todolist.backend.security.util.JwtService;
import com.todolist.backend.service.TodoListService;
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

import static org.hamcrest.Matchers.*;
import static com.todolist.backend.entity.Roles.Constants.ROLE_USER;
import static org.junit.Assert.assertNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TodoListControllerIntegrationTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    TodoListService todoListService;

    @Autowired
    UserService userService;

    @Autowired
    TodoListController todoListController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private User testUser;

    private TodoList testList;

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

        testList = new TodoList();
        testList.setName("list1");
        testList = todoListService.save(testUser, testList);
    }

    @After
    public void tearDown() {
        todoListService.deleteAll();
        userService.deleteAll();
        roleRepo.deleteAll();
    }

    // -----------------------------------------------------
    //             -- Test for CREATE TODOLIST
    // -----------------------------------------------------
    @Test
    public void givenValidTodoListDataAndValidUserIdWithToken_whenPostRequest_thenShouldResponseCreated() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListData = "{\"name\": \"todolist\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/lists")
                .header("Authorization", "Bearer " + generateToken())
                .content(todoListData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.list.name", is("todolist")));
    }

    @Test
    public void givenValidTodoListDataAndInvalidUserIdWithToken_whenPostRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId() + 1);
        String todoListData = "{\"name\": \"todolist\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/lists")
                .header("Authorization", "Bearer " + generateToken())
                .content(todoListData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenValidTodoListDataAndValidUserIdWithoutToken_whenPostRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListData = "{\"name\": \"todolist\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/lists")
                .content(todoListData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //             -- Test for GET ALL LISTS
    // -----------------------------------------------------
    @Test
    public void givenValidUserIdWithToken_whenGetRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId + "/lists")
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.lists", hasSize(1)));
    }

    @Test
    public void givenInvalidUserIdWithToken_whenGetRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId() + 1);

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId + "/lists")
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenValidUserIdWithoutToken_whenGetRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId + "/lists"));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //             -- Test for UPDATE LIST
    // -----------------------------------------------------
    @Test
    public void givenValidUserIdAndValidListIdWithToken_whenPutRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String listId = Integer.toString(testList.getId());
        String listNewName = "updateList";
        String listNewData = "{ \"name\": \"" + listNewName + "\" }";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + userId + "/lists/" + listId)
                .header("Authorization", "Bearer " + generateToken())
                .content(listNewData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.list.name", is(listNewName)));
    }

    @Test
    public void givenInvalidUserIdAndValidListIdWithToken_whenPutRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId() + 1);
        String listId = Integer.toString(testList.getId());
        String listNewName = "updateList";
        String listNewData = "{ \"name\": \"" + listNewName + "\" }";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + userId + "/lists/" + listId)
                .header("Authorization", "Bearer " + generateToken())
                .content(listNewData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenValidUserIdAndInvalidListIdWithToken_whenPutRequest_thenShouldResponseNotFound() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String listId = Integer.toString(testList.getId() + 1);
        String listNewName = "updateList";
        String listNewData = "{ \"name\": \"" + listNewName + "\" }";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + userId + "/lists/" + listId)
                .header("Authorization", "Bearer " + generateToken())
                .content(listNewData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void givenValidUserIdAndValidListIdWithoutToken_whenPutRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String listId = Integer.toString(testList.getId());
        String listNewName = "updateList";
        String listNewData = "{ \"name\": \"" + listNewName + "\" }";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + userId + "/lists/" + listId)
                .content(listNewData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //             -- Test for DELETE LIST
    // -----------------------------------------------------
    @Test
    public void givenValidUserIdAndValidListIdWithToken_whenDeleteRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String listId = Integer.toString(testList.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/lists/" + listId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
        assertNull(todoListService.getByIdAndUserId(testList.getId(), testUser.getId()));
    }

    @Test
    public void givenInvalidUserIdAndValidListIdWithToken_whenDeleteRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId() + 1);
        String listId = Integer.toString(testList.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/lists/" + listId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenValidUserIdAndInvalidListIdWithToken_whenDeleteRequest_thenShouldResponseNotFound() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String listId = Integer.toString(testList.getId() + 1);

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/lists/" + listId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void givenValidUserIdAndValidListIdWithoutToken_whenDeleteRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String listId = Integer.toString(testList.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/lists/" + listId));

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