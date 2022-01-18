package com.todolist.backend.repository;

import com.todolist.backend.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("select i from Item i join TodoList t on i.todoList.id = t.id where i.state = false and i.todoList.id = :todoListId" /*and i.expired is null or i.expired > :timestamp and t.user.id = :userId*/)
    Page<Item> findAllUnchecked(
            @Param("todoListId") int todoListId, /*@Param("timestamp") Timestamp timestamp,
            @Param("userId") int userId,*/ Pageable pageable);

    Page<Item> findAllByTodoListIdAndStateFalseAndExpiredBeforeAndTodoList_UserId(
            int todoListId, Timestamp timestamp, int userId, Pageable pageable);

    Page<Item> findAllByTodoListIdAndStateTrueAndTodoList_UserId(
            int todoListId, int userId, Pageable pageable);
}
