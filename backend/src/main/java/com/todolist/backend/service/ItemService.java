package com.todolist.backend.service;

import com.todolist.backend.entity.Item;
import com.todolist.backend.entity.TodoList;
import com.todolist.backend.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepo;

    public Item save(Item item, TodoList todoList){
        item.setTodoList(todoList);
        return itemRepo.save(item);
    }

    public Item getByIdAndUserId(int itemId, int userId){
        return itemRepo.findByIdAndTodoList_UserId(itemId, userId);
    }

    public List<Item> getAllByTodoListIdAndUserId(int todoListId, int userId){
        return itemRepo.findAllByTodoListIdAndTodoList_UserId(todoListId, userId);
    }
}
