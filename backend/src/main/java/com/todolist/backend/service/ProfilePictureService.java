package com.todolist.backend.service;

import com.todolist.backend.entity.ProfilePicture;
import com.todolist.backend.entity.User;
import com.todolist.backend.repository.ProfilePictureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfilePictureService {

    private final ProfilePictureRepository profilePictureRepo;

    public ProfilePicture save(User user, ProfilePicture profilePicture){
        profilePicture.setUser(user);
        return profilePictureRepo.save(profilePicture);
    }

    public void delete(ProfilePicture profilePicture){
        profilePictureRepo.delete(profilePicture);
    }
}
