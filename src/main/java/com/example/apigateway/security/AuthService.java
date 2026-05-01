package com.example.apigateway.security;

import com.example.apigateway.dto.RegisterRequest;
import com.example.apigateway.model.User;
import com.example.apigateway.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<String> authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return Optional.of(username);
        }
        return Optional.empty();
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User newUser = new User(request.getUsername(), request.getPassword(), request.getEmail());
        userRepository.save(newUser);
    }
}

