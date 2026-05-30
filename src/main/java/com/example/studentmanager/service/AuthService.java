package com.example.studentmanager.service;

import com.example.studentmanager.model.User;
import com.example.studentmanager.repository.UserRepository;
import com.example.studentmanager.util.PasswordUtil;
import com.example.studentmanager.util.SessionManager;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepo = new UserRepository();

    public boolean login(String username, String password) {
        Optional<User> optUser = userRepo.findByUsername(username);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (PasswordUtil.verify(password, user.getPasswordHash())) {
                SessionManager.getInstance().setLoggedInUsername(username);
                return true;
            }
        }
        return false;
    }

    public void register(String username, String password) {
        if (userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken.");
        }
        String hashed = PasswordUtil.hash(password);
        userRepo.save(new User(0, username, hashed));
    }
}
