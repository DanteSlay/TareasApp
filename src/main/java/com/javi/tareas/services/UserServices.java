package com.javi.tareas.services;

import com.javi.tareas.entities.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServices {
    private HashMap<String, User> userRepository = new HashMap<>();

    @PostConstruct
    public void init() {
        User u = User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password("root1234")
                .build();
        userRepository.put(u.getEmail(), u);
    }

    public User get(String email) {
        return userRepository.get(email);
    }

    public User add(User newUser) {
        userRepository.put(newUser.getEmail(), newUser);
        return newUser;
    }

    public boolean emailExist(String email) {
        return userRepository.get(email) != null;
    }

    public boolean passwordCorrect(User user, String password) {
        return user.getPassword().equals(password);
    }

    public boolean authenticationFail(String email, String password) {
        if (emailExist(email)) {
            User user = get(email);
            return !passwordCorrect(user, password);
        }
        return true;
    }

    public boolean usernameExist(String username) {
        User usernameExist = userRepository.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findAny().orElse(null);

        return usernameExist != null;
    }

    public Map<String, String> newUserValidation(User newUser, String repeatPassword) {

        Map<String, String> errors = new HashMap<>();

        if (!newUser.getPassword().equals(repeatPassword)) errors.put("password", "user.password2.error");

        if (usernameExist(newUser.getUsername())) errors.put("username", "user.nameExist.error");

        if (emailExist(newUser.getEmail())) errors.put("email", "user.emailExist.error");

        return errors;
    }

}
