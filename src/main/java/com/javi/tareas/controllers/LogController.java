package com.javi.tareas.controllers;

import com.javi.tareas.entities.MyUser;
import com.javi.tareas.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Proporciona métodos para iniciar sesión, recuperar contraseñas y registrarse.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LogController {
    private final UserService userService;

    /**
     * Muesta la página de Inicio de sesión
     */
    @GetMapping( "")
    public String signIn() {
        return "logIn/index";
    }

    @GetMapping("/logout")
    public String logOut(HttpSession session) {
        session.invalidate();
        return "logIn/index";
    }


    /**
     * Muestra la página de registro de nuevos usuarios
     */
    @GetMapping("/newUser")
    public String register(Model model) {
        model.addAttribute("newMyUser", new MyUser());
        return "logIn/register";
    }

    /**
     * Procesa el formulario de registro de nuevos usuarios.
     */
    @PostMapping("/register/submit")
    public String signUp(@RequestParam("repeatPassword") String repeatPass,@Valid @ModelAttribute("newMyUser") MyUser newMyUser,
                         BindingResult result) {
        Map<String, String> errors = userService.newUserValidation(newMyUser, repeatPass);

        for (Map.Entry<String, String> error : errors.entrySet()) {
            result.rejectValue(error.getKey(), error.getValue());
        }

        if (result.hasErrors()) {
            return "logIn/register";
        }
        newMyUser.setRole("ROLE_USER");
        userService.save(newMyUser);
        return "redirect:/login";
    }
}
