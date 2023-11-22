package com.javi.tareas.controllers;

import com.javi.tareas.entities.MyUser;
import com.javi.tareas.services.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Clase controladora que gestiona las operaciones relacionadas con usuarios en el sistema.
 * Proporciona métodos para iniciar sesión, recuperar contraseñas, registrarse y realizar validaciones de usuario.
 */
@Slf4j // Anotación que habilita la funcionalidad de registro (logging) en la clase.
@Controller // Marca esta clase como un controlador de Spring MVC.
public class UserController {
    @Autowired // Inyecta una instancia del bean UserServices
    private UserService userService;

    /**
     * Muesta la página de Inicio de sesión
     *
     * @return La vista "LogIn" para iniciar sesión
     */
    @GetMapping( "/login")
    public String signIn() {
        return "logIn/index";
    }

    @GetMapping("/logout")
    public String logOut(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /**
     * Muestra una página con los datos de un Usuario predeterminado para facilitar el inicio de sesión
     *
     * @param model El modelo utilizado para pasar los datos a la vista
     * @return La vista "users" que proporciona información del usuario predeterminado
     */
    @GetMapping("/login/forgotPassword")
    public String forgotPasswords(HttpSession session, Model model) {
        MyUser myUserDefault = userService.findByEmail("admin@gmail.com");
        model.addAttribute("userDt", myUserDefault);
        session.setAttribute("user", myUserDefault.getId());
        return "logIn/users";
    }

    /**
     * Muestra la página de registro de nuevos usuarios
     *
     * @param model El modelo utilizado para pasar datos a la vista
     * @return La vista "register" que contiene un formulario para registrar nuevos usuarios
     */
    @GetMapping("/login/newUser")
    public String register(Model model) {
        model.addAttribute("newUser", new MyUser());
        return "logIn/register";
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
//    @PostMapping("/login/submit")
//    public String signIn(@RequestParam("username") String email, @RequestParam("password") String password
//                        , HttpSession session
//                        , Model model) {
//        if (!userService.authenticationSuccess(email, password)) {
//            model.addAttribute("error", "user.authenticationFail.error");
//            return "logIn/index";
//        }
//
//        User user = userService.get(email);
//        session.setAttribute("user", user.getId());
//        return "redirect:/home/" + user.getId();
//    }

    /**
     * Procesa el formulario de registro de nuevos usuarios.
     * Realiza validaciones y muestra mensajes de error si es necesario.
     * Si el registro es exitoso, redirige al usuario a la página de inicio de sesión.
     *
     * @param repeatPass La contraseña repetida proporcionada en el formulario de registro.
     * @param newMyUser    El nuevo usuario que se desea registrar proporcionado en el formulario de registro.
     * @param result     El resultado de las validaciones que se utiliza para detectar errores.
     * @return Si hay errores de validación, muestra la vista "register". Si el registro es exitoso, redirige al usuario a la vista "logIn".
     */
    @PostMapping("/login/register/submit")
    public String signUp(@RequestParam("repeatPassword") String repeatPass, @ModelAttribute("newUser") MyUser newMyUser,
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
