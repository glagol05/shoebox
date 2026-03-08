package com.example.services;

import java.security.Principal;

import org.springframework.stereotype.Service;

import com.example.models.User;
import com.example.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOrCreate(Principal principal) {
        String username = principal.getName();
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPassword("N/A");
                    return userRepository.save(newUser);
                });
    }
}