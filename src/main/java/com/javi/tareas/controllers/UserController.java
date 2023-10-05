package com.javi.tareas.controllers;

import com.javi.tareas.entities.User;
import com.javi.tareas.services.UserServices;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Slf4j
@Controller
public class UserController {
    @Autowired
    private UserServices userService;

    @GetMapping({"/", "/logIn"})
    public String signIn() {
        return "logIn";
    }

    @GetMapping("/forgotPassword")
    public String forgotPasswords(Model model) {
        model.addAttribute("userDt", userService.get("admin@gmail.com"));
        return "users";
    }

    @GetMapping("/newUser")
    public String register(Model model) {
        model.addAttribute("newUser", new User());
        return "register";
    }

    @PostMapping("/logIn/submit")
    public String signIn(@RequestParam("email") String email, @RequestParam("password") String password
            , Model model) {
        if (userService.authenticationFail(email, password)) {
            model.addAttribute("error", "user.authenticationFail.error");
            return "logIn";
        }

        User user = userService.get(email);
        return "redirect:/home/" + user.getId();
    }

    @PostMapping("/register/submit")
    public String signUp(@RequestParam("repeatPassword") String repeatPass, @Valid @ModelAttribute("newUser") User newUser,
                         BindingResult result) {
        Map<String, String> errors = userService.newUserValidation(newUser, repeatPass);

        for (Map.Entry<String, String> error : errors.entrySet()) {
            result.rejectValue(error.getKey(), error.getValue());
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.add(newUser);
        return "redirect:/logIn";
    }
}
