package com.todolist.backend.controller;

import com.todolist.backend.controller.util.ResponseEntityUtil;
import com.todolist.backend.dto.todolist.TodoListDto;
import com.todolist.backend.dto.todolist.TodoListGetDto;
import com.todolist.backend.entity.TodoList;
import com.todolist.backend.entity.User;
import com.todolist.backend.service.TodoListService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import static com.todolist.backend.entity.Roles.Constants.ROLE_USER;

@RestController
@RequiredArgsConstructor
@Secured(ROLE_USER)
@RequestMapping(path = "/users/{userId}/lists", produces = MediaType.APPLICATION_JSON_VALUE)
public class TodoListController {

    private final TodoListService todoListService;

    private final ModelMapper mapper;

    @PostMapping()
    public ResponseEntity<Map<String, Object>> createList(
            Principal principal,
            @PathVariable("userId") Integer userId,
            @Valid @RequestBody TodoListDto requestList) {

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            return ResponseEntityUtil.generateResponse(HttpStatus.FORBIDDEN,
                    "errorMessage", "Login to create list");
        }

        User currentUser = new User();
        currentUser.setId(userId);
        TodoList list = mapper.map(requestList, TodoList.class);
        list = todoListService.save(currentUser, list);
        TodoListGetDto todoListGetDto = mapper.map(list, TodoListGetDto.class);

        return ResponseEntityUtil.generateResponse(HttpStatus.CREATED, "list", todoListGetDto);
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAllLists(Principal principal, @PathVariable("userId") Integer userId) {

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            return ResponseEntityUtil.generateResponse(HttpStatus.FORBIDDEN,
                    "errorMessage", "Login to get lists");
        }

        List<TodoList> lists = todoListService.getAllByUserId(userId);
        List<TodoListGetDto> listsDto = mapper.map(lists, new TypeToken<List<TodoListGetDto>>() {
        }.getType());

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, "lists", listsDto);
    }

    @PutMapping("/{listId}")
    public ResponseEntity<Map<String, Object>> updateList(
            Principal principal,
            @PathVariable("userId") Integer userId,
            @PathVariable("listId") Integer listId,
            @Valid @RequestBody TodoListDto requestList) {

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            return ResponseEntityUtil.generateResponse(HttpStatus.FORBIDDEN,
                    "errorMessage", "Login to update list");
        }

        TodoList currentList = todoListService.getByIdAndUserId(listId, userId);
        if(currentList == null){
            return ResponseEntityUtil.generateResponse(HttpStatus.NOT_FOUND,
                    "errorMessage", "The list does not exist");
        }

        currentList.setName(requestList.getName());
        currentList = todoListService.update(currentList);
        TodoListGetDto todoListGetDto = mapper.map(currentList, TodoListGetDto.class);

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, "list", todoListGetDto);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Map<String, Object>> updateList(
            Principal principal,
            @PathVariable("userId") Integer userId,
            @PathVariable("listId") Integer listId) {

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            return ResponseEntityUtil.generateResponse(HttpStatus.FORBIDDEN,
                    "errorMessage", "Login to delete list");
        }

        TodoList currentList = todoListService.getByIdAndUserId(listId, userId);
        if(currentList == null){
            return ResponseEntityUtil.generateResponse(HttpStatus.NOT_FOUND,
                    "errorMessage", "The list does not exist");
        }

        todoListService.delete(currentList);

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, null, null);
    }
}
