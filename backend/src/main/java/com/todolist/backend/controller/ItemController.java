package com.todolist.backend.controller;

import com.todolist.backend.controller.util.ResponseEntityUtil;
import com.todolist.backend.dto.item.ItemDto;
import com.todolist.backend.dto.item.ItemGetDto;
import com.todolist.backend.dto.item.PageItemsDto;
import com.todolist.backend.entity.Item;
import com.todolist.backend.entity.TodoList;
import com.todolist.backend.service.ItemService;
import com.todolist.backend.service.TodoListService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

import static com.todolist.backend.entity.Roles.Constants.ROLE_USER;

@RestController
@RequiredArgsConstructor
@Secured(ROLE_USER)
@RequestMapping(path = "/users/{userId}/lists/{listId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemController {

    @Value("${pagination.items.number}")
    int items_per_page;

    private final ItemService itemService;

    private final TodoListService todoListService;

    private final ModelMapper mapper;

    @PostMapping()
    public ResponseEntity<Map<String, Object>> createItem(
            Principal principal,
            @PathVariable("userId") Integer userId,
            @PathVariable("listId") Integer listId,
            @Valid @RequestBody ItemDto requestItem) {

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            return ResponseEntityUtil.generateResponse(HttpStatus.FORBIDDEN,
                    "errorMessage", "Login to create item");
        }

        TodoList currentList = todoListService.getByIdAndUserId(listId, userId);
        if(currentList == null){
            return ResponseEntityUtil.generateResponse(HttpStatus.NOT_FOUND,
                    "errorMessage", "The list does not exist");
        }

        Item item = mapper.map(requestItem, Item.class);
        item = itemService.save(item, currentList);
        ItemGetDto itemGetDto = mapper.map(item, ItemGetDto.class);

        return ResponseEntityUtil.generateResponse(HttpStatus.CREATED, "item", itemGetDto);
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAllItemsByTodoListId(
            Principal principal,
            @PathVariable("userId") Integer userId,
            @PathVariable("listId") Integer listId,
            @RequestParam(value = "page", required = false) Integer pageNumber,
            @RequestParam(value = "state", required = false) String state) {

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            return ResponseEntityUtil.generateResponse(HttpStatus.FORBIDDEN,
                    "errorMessage", "Login to get items");
        }

        pageNumber = pageNumber == null ? 0 : pageNumber;
        Pageable pageable = PageRequest.of(pageNumber, items_per_page);

        Page<Item> items;
        if (state == null || !state.equals("checked") && !state.equals("expired")) {
            items = itemService.getAllItemsUnChecked(listId, userId, pageable);
        }else if(state.equals("checked")){
            items = itemService.getAllItemsChecked(listId, userId, pageable);
        }else{
            items = itemService.getAllItemsExpired(listId, userId, pageable);
        }

        PageItemsDto pageItemsDto = mapper.map(items, PageItemsDto.class);

        return ResponseEntityUtil.generateResponse(HttpStatus.OK, "items", pageItemsDto);
    }
}
