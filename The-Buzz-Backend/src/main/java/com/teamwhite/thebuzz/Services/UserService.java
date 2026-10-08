package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.UserRepository;
import com.teamwhite.thebuzz.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User getUserRecordByUsername(String username) {
        return repository.getUserRecordByUsername(username);
    }
}
