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

    public Item getByIdAndListIdAndUserId(int id, int listId, int userId) {
        return itemRepo.findByIdAndTodoListIdAndTodoList_UserId(id, listId, userId);
    }

    public Page<Item> getAllItemsUnchecked(int todoListId, int userId, Pageable pageable) {
        return itemRepo.findItemsUnchecked(todoListId, Timestamp.from(Instant.now()), userId, pageable);
    }

    public Page<Item> getAllItemsExpired(int todoListId, int userId, Pageable pageable) {
        return itemRepo.findByTodoListIdAndStateAndExpiredBeforeAndTodoList_UserId(
                todoListId, false, Timestamp.from(Instant.now()), userId, pageable);
    }

    public Page<Item> getAllItemsChecked(int todoListId, int userId, Pageable pageable) {
        return itemRepo.findByTodoListIdAndStateAndTodoList_UserId(todoListId, true, userId, pageable);
    }

    public Item update(Item item, String text, Timestamp expired, boolean state) {
        item.setText(text);
        item.setExpired(expired);
        item.setState(state);
        return itemRepo.save(item);
    }

    public void delete(Item item) {
        itemRepo.delete(item);
    }

    public void deleteAll() {
        itemRepo.deleteAll();
    }
}
