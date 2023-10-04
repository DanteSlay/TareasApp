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

    @GetMapping({"/", "/signIn"})
    public String signIn(Model model) {
        model.addAttribute("userDt", new User());
        return "sign-in";
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

    @PostMapping("/signIn/submit")
    public String signIn(@Valid @ModelAttribute("userDt")User user, BindingResult result) {
        if (!userService.emailExist(user.getEmail())) {
            result.rejectValue("email", "user.emailNotFound.error");
        } else if (!userService.passwordCorrect(user)) {
            result.rejectValue("password", "user.passwordNotFound.error");
        }

        if (result.hasErrors()) return "sign-in";

        return "redirect:/home";
    }

    @PostMapping("/register/submit")
    public String signUp(@RequestParam("repeatPassword") String repeatPass, @Valid @ModelAttribute("newUser") User newUser,
                         BindingResult result) {
        log.info("Entrando en el post");
        log.info(String.valueOf(result.hasErrors()));
        log.info(String.valueOf(result.getAllErrors()));
        Map<String, String> errors = userService.newUserValidation(newUser, repeatPass);

        for (Map.Entry<String, String> error : errors.entrySet()) {
            result.rejectValue(error.getKey(), error.getValue());
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.add(newUser);
        return "redirect:/signIn";
    }
}
