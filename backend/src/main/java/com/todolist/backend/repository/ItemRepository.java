package com.todolist.backend.repository;

import com.todolist.backend.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("select i from Item i where i.state = false and (i.expired is null or i.expired > :timestamp) and i.todoList.id = :todoListId and i.todoList.user.id = :userId")
    Page<Item> findItemsUnchecked(@Param("todoListId") int todoListId, @Param("timestamp") Timestamp timestamp, @Param("userId") int userId, Pageable pageable);

    Page<Item> findByTodoListIdAndStateAndExpiredBeforeAndTodoList_UserId(
            int todoListId, boolean state, Timestamp timestamp, int userId, Pageable pageable);

    Page<Item> findByTodoListIdAndStateAndTodoList_UserId(
            int todoListId, boolean state, int userId, Pageable pageable);

    Item findByIdAndTodoListIdAndTodoList_UserId(int id, int todoListId, int userId);
}
