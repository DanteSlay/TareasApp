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

/**
 * Clase controladora que gestiona las operaciones relacionadas con usuarios en el sistema.
 * Proporciona métodos para iniciar sesión, recuperar contraseñas, registrarse y realizar validaciones de usuario.
 */
@Slf4j // Anotación que habilita la funcionalidad de registro (logging) en la clase.
@Controller // Marca esta clase como un controlador de Spring MVC.
public class UserController {
    @Autowired // Inyecta una instancia del bean UserServices
    private UserServices userService;

    /**
     * Muesta la página de Inicio de sesión
     *
     * @return La vista "LogIn" para iniciar sesión
     */
    @GetMapping({"/", "/logIn"})
    public String signIn() {
        return "logIn";
    }

    /**
     * Muestra una página con los datos de un Usuario predeterminado para facilitar el inicio de sesión
     *
     * @param model El modelo utilizado para pasar los datos a la vista
     * @return La vista "users" que proporciona información del usuario predeterminado
     */
    @GetMapping("/forgotPassword")
    public String forgotPasswords(Model model) {
        model.addAttribute("userDt", userService.get("admin@gmail.com"));
        return "users";
    }

    /**
     * Muestra la página de registro de nuevos usuarios
     *
     * @param model El modelo utilizado para pasar datos a la vista
     * @return La vista "register" que contiene un formulario para registrar nuevos usuarios
     */
    @GetMapping("/newUser")
    public String register(Model model) {
        model.addAttribute("newUser", new User());
        return "register";
    }

    /**
     * Procesa el formulario de inicio de sesión y autentifica a los usuarios.
     * Si la autenticación falla, muestra un mensaje de error en la vista.
     *
     * @param email    El correo electrónico proporcionado en el formulario de inicio de sesión.
     * @param password La contraseña proporcionada en el formulario de inicio de sesión.
     * @param model    El modelo utilizado para pasar datos a la vista.
     * @return Si la autenticación es exitosa, redirige al usuario a su página de inicio. Si falla, muestra un mensaje de error en la vista "logIn".
     */
    @PostMapping("/logIn/submit")
    public String signIn(@RequestParam("email") String email, @RequestParam("password") String password
            , Model model) {
        if (!userService.authenticationSuccess(email, password)) {
            model.addAttribute("error", "user.authenticationFail.error");
            return "logIn";
        }

        User user = userService.get(email);
        return "redirect:/home/" + user.getId();
    }

    /**
     * Procesa el formulario de registro de nuevos usuarios.
     * Realiza validaciones y muestra mensajes de error si es necesario.
     * Si el registro es exitoso, redirige al usuario a la página de inicio de sesión.
     *
     * @param repeatPass La contraseña repetida proporcionada en el formulario de registro.
     * @param newUser    El nuevo usuario que se desea registrar proporcionado en el formulario de registro.
     * @param result     El resultado de las validaciones que se utiliza para detectar errores.
     * @return Si hay errores de validación, muestra la vista "register". Si el registro es exitoso, redirige al usuario a la vista "logIn".
     */
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
