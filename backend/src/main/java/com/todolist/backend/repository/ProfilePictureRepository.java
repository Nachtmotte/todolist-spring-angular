package com.todolist.backend.repository;

import com.todolist.backend.entity.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Integer> {
}
