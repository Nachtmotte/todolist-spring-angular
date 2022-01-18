package com.todolist.backend.service;

import com.todolist.backend.entity.Item;
import com.todolist.backend.entity.TodoList;
import com.todolist.backend.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepo;

    public Item save(Item item, TodoList todoList) {
        item.setTodoList(todoList);
        return itemRepo.save(item);
    }

    public Page<Item> getAllItemsUnChecked(int todoListId, int userId, Pageable pageable) {
        return itemRepo.findAllUnchecked(todoListId, /*Timestamp.from(Instant.now()), userId,*/ pageable);
    }

    public Page<Item> getAllItemsExpired(int todoListId, int userId, Pageable pageable){
        return itemRepo.findAllByTodoListIdAndStateFalseAndExpiredBeforeAndTodoList_UserId(
                todoListId, Timestamp.from(Instant.now()), userId, pageable);
    }

    public Page<Item> getAllItemsChecked(int todoListId, int userId, Pageable pageable){
        return itemRepo.findAllByTodoListIdAndStateTrueAndTodoList_UserId(todoListId, userId, pageable);
    }

    public void deleteAll() {
        itemRepo.deleteAll();
    }
}
