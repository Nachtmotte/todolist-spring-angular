package com.todolist.backend.controller;

import com.todolist.backend.entity.Item;
import com.todolist.backend.entity.Role;
import com.todolist.backend.entity.TodoList;
import com.todolist.backend.entity.User;
import com.todolist.backend.repository.RoleRepository;
import com.todolist.backend.security.util.JwtService;
import com.todolist.backend.service.ItemService;
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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import static com.todolist.backend.entity.Roles.Constants.ROLE_USER;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemControllerIntegrationTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    ItemService itemService;

    @Autowired
    TodoListService todoListService;

    @Autowired
    UserService userService;

    @Autowired
    ItemController itemController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private User testUser;

    private TodoList testList;

    private Item testItemUnchecked;

    private Item testItemChecked;

    private Item testItemExpired;

    @Before
    public void setup() throws ParseException {
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
        testList = todoListService.save(testList, testUser);

        testItemUnchecked = new Item();
        testItemUnchecked.setText("unchecked");
        testItemUnchecked = itemService.save(testItemUnchecked, testList);

        testItemChecked = new Item();
        testItemChecked.setText("checked");
        testItemChecked.setState(true);
        testItemChecked = itemService.save(testItemChecked, testList);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse("01/01/2000");
        long time = date.getTime();
        testItemExpired = new Item();
        testItemExpired.setText("expired");
        testItemExpired.setExpired(new Timestamp(time));
        testItemExpired = itemService.save(testItemExpired, testList);
    }

    @After
    public void tearDown() {
        itemService.deleteAll();
        todoListService.deleteAll();
        userService.deleteAll();
        roleRepo.deleteAll();
    }

    // -----------------------------------------------------
    //             -- Test for CREATE ITEM
    // -----------------------------------------------------
    @Test
    public void givenValidUserIdAndValidTodoListIdWithToken_whenPostRequest_thenShouldResponseCreated() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListId = Integer.toString(testList.getId());
        String itemData = "{\"text\": \"item\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/lists/" + todoListId + "/items")
                .header("Authorization", "Bearer " + generateToken())
                .content(itemData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.item.text", is("item")));
    }

    @Test
    public void givenInvalidUserIdAndValidTodoListIdWithToken_whenPostRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId() + 1);
        String todoListId = Integer.toString(testList.getId());
        String itemData = "{\"text\": \"item\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/lists/" + todoListId + "/items")
                .header("Authorization", "Bearer " + generateToken())
                .content(itemData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenValidUserIdAndInvalidTodoListIdWithToken_whenPostRequest_thenShouldResponseNotFound() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListId = Integer.toString(testList.getId() + 1);
        String itemData = "{\"text\": \"item\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/lists/" + todoListId + "/items")
                .header("Authorization", "Bearer " + generateToken())
                .content(itemData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void givenValidUserIdAndValidTodoListIdWithoutToken_whenPostRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListId = Integer.toString(testList.getId());
        String itemData = "{\"text\": \"item\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users/" + userId + "/lists/" + todoListId + "/items")
                .content(itemData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //             -- Test for GET ITEMS
    // -----------------------------------------------------
    @Test
    public void givenValidUserIdAndValidTodoListIdWithTokenAndWithoutParam_whenGetRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListId = Integer.toString(testList.getId());

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId + "/lists/" + todoListId + "/items")
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.items.content", hasSize(1)));
        result.andExpect(jsonPath("$.items.content[0].text", is(testItemUnchecked.getText())));
    }

    @Test
    public void givenValidUserIdAndValidTodoListIdWithTokenAndWithParamChecked_whenGetRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListId = Integer.toString(testList.getId());

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId + "/lists/" + todoListId + "/items" + "?state=checked")
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.items.content", hasSize(1)));
        result.andExpect(jsonPath("$.items.content[0].text", is(testItemChecked.getText())));
    }

    @Test
    public void givenValidUserIdAndValidTodoListIdWithTokenAndWithParamExpired_whenGetRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListId = Integer.toString(testList.getId());

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId + "/lists/" + todoListId + "/items" + "?state=expired")
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.items.content", hasSize(1)));
        result.andExpect(jsonPath("$.items.content[0].text", is(testItemExpired.getText())));
    }

    @Test
    public void givenInvalidUserIdAndValidTodoListIdWithToken_whenGetRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId() + 1);
        String todoListId = Integer.toString(testList.getId());

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId + "/lists/" + todoListId + "/items")
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenValidUserIdAndInvalidTodoListIdWithToken_whenGetRequest_thenShouldResponseNotFound() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListId = Integer.toString(testList.getId() + 1);

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId + "/lists/" + todoListId + "/items")
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void givenValidUserIdAndValidTodoListIdWithoutToken_whenGetRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListId = Integer.toString(testList.getId());

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId + "/lists/" + todoListId + "/items"));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //             -- Test for UPDATE ITEM
    // -----------------------------------------------------
    @Test
    public void givenValidUserIdAndValidTodoListIdAndValidItemIdWithToken_whenPutRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListId = Integer.toString(testList.getId());
        String itemId = Integer.toString(testItemUnchecked.getId());
        String itemNewData = "{\"text\": \"newText\"," +
                "\"state\": \"false\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + userId + "/lists/" + todoListId + "/items/" + itemId)
                .header("Authorization", "Bearer " + generateToken())
                .content(itemNewData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.item.text", is("newText")));
    }

    @Test
    public void givenInvalidUserIdAndValidTodoListIdAndValidItemIdWithToken_whenPutRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId() + 1);
        String todoListId = Integer.toString(testList.getId());
        String itemId = Integer.toString(testItemUnchecked.getId());
        String itemNewData = "{\"text\": \"newText\"," +
                "\"state\": \"false\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + userId + "/lists/" + todoListId + "/items/" + itemId)
                .header("Authorization", "Bearer " + generateToken())
                .content(itemNewData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenValidUserIdAndInvalidTodoListIdAndValidItemIdWithToken_whenPutRequest_thenShouldResponseNotFound() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListId = Integer.toString(testList.getId() + 1);
        String itemId = Integer.toString(testItemUnchecked.getId());
        String itemNewData = "{\"text\": \"newText\"," +
                "\"state\": \"false\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + userId + "/lists/" + todoListId + "/items/" + itemId)
                .header("Authorization", "Bearer " + generateToken())
                .content(itemNewData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void givenValidUserIdAndValidTodoListIdAndInvalidItemIdWithToken_whenPutRequest_thenShouldResponseNotFound() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListId = Integer.toString(testList.getId());
        String itemId = Integer.toString(testItemUnchecked.getId() + 10);
        String itemNewData = "{\"text\": \"newText\"," +
                "\"state\": \"false\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + userId + "/lists/" + todoListId + "/items/" + itemId)
                .header("Authorization", "Bearer " + generateToken())
                .content(itemNewData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void givenValidUserIdAndValidTodoListIdAndValidItemIdWithoutToken_whenPutRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String todoListId = Integer.toString(testList.getId());
        String itemId = Integer.toString(testItemUnchecked.getId());
        String itemNewData = "{\"text\": \"newText\"," +
                "\"state\": \"false\"}";

        //when
        ResultActions result = mockMvc.perform(put("/users/" + userId + "/lists/" + todoListId + "/items/" + itemId)
                .content(itemNewData)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isForbidden());
    }

    // -----------------------------------------------------
    //             -- Test for DELETE LIST
    // -----------------------------------------------------
    @Test
    public void givenValidUserIdAndValidListIdAndValidItemIdWithToken_whenDeleteRequest_thenShouldResponseOk() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String listId = Integer.toString(testList.getId());
        String itemId = Integer.toString(testItemUnchecked.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/lists/" + listId + "/items/" + itemId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
        assertNull(itemService.getByIdAndListIdAndUserId(testItemUnchecked.getId(), testList.getId(), testUser.getId()));
    }

    @Test
    public void givenInvalidUserIdAndValidListIdAndValidItemIdWithToken_whenDeleteRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId() + 1);
        String listId = Integer.toString(testList.getId());
        String itemId = Integer.toString(testItemUnchecked.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/lists/" + listId + "/items/" + itemId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenValidUserIdAndInvalidListIdAndValidItemIdWithToken_whenDeleteRequest_thenShouldResponseNotFound() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String listId = Integer.toString(testList.getId() + 1);
        String itemId = Integer.toString(testItemUnchecked.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/lists/" + listId + "/items/" + itemId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void givenValidUserIdAndValidListIdAndInvalidItemIdWithToken_whenDeleteRequest_thenShouldResponseNotFound() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String listId = Integer.toString(testList.getId());
        String itemId = Integer.toString(testItemUnchecked.getId() + 10);

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/lists/" + listId + "/items/" + itemId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void givenValidUserIdAndValidListIdAndValidItemIdWithoutToken_whenDeleteRequest_thenShouldResponseForbidden() throws Exception {

        //given
        String userId = Integer.toString(testUser.getId());
        String listId = Integer.toString(testList.getId());
        String itemId = Integer.toString(testItemUnchecked.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/users/" + userId + "/lists/" + listId + "/items/" + itemId));
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