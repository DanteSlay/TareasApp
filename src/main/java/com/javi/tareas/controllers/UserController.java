package com.javi.tareas.controllers;

import com.javi.tareas.entities.User;
import com.javi.tareas.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        model.addAttribute("userDt", userService.forgotPassword());
        return "users";
    }

    @PostMapping("/signIn")
    public String signIn(@RequestParam("email") String email, @RequestParam("password") String password) {
        return userService.userVerification(email, password) != null ? "redirect:/home" : "sign-in";
    }
}
