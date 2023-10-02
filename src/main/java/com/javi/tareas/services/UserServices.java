package com.javi.tareas.services;

import com.javi.tareas.entities.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserServices {
    private HashMap<String, User> userRepository = new HashMap<>();

    @PostConstruct
    public void init() {
        User u = User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password("root")
                .build();
        userRepository.put(u.getEmail(), u);
    }

    public User forgotPassword() {
        return userRepository.get("admin@gmail.com");
    }

    public User userVerification(String email, String password) {
        User userFound = userRepository.get(email);

        if (userFound != null) {
            if (userFound.getEmail().equals(email) &&
                    userFound.getPassword().equals(password)) {
                return userFound;
            }
        }
        return null;
    }

}
