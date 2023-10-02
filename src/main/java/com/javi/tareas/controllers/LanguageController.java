package com.javi.tareas.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LanguageController {
    @GetMapping("/changeLanguage")

    public String changeLanguage(@RequestParam("lang") String lang, HttpServletRequest request) {
        // Establecemos el idioma que selecciono el usuario
        request.getSession().setAttribute("lang", lang);

        // Redirecciona a la página actual o a la página de inicio, según corresponda
        String referer = request.getHeader("Referer");

        return "redirect:" + (referer != null ? referer : "/");
    }
}
