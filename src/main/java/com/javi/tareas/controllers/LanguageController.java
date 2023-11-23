package com.javi.tareas.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Clase controladora que maneja el cambio de idioma en la aplicación.
 */
@Controller
public class LanguageController {

    /**
     * Método que se encarga de cambiar el idioma de la aplicación según la selección del usuario (Español O Ingles).
     *
     */
    @GetMapping("/changeLanguage")
    public String changeLanguage(@RequestParam("lang") String lang, HttpServletRequest request) {
        // Establece el idioma seleccionado por el usuario en la sesión actual
        request.getSession().setAttribute("lang", lang);

        // Obtiene la URL de la página anterior (Referer) para redireccionar allí
        String referer = request.getHeader("Referer");

        //Si venimos de un submit(PostMapping) volveremos a la URL del método GET
        if (referer.contains("submit")) {
            return "redirect:" + referer.replace("/submit", "");
        }

        // Retorna una redirección a la página anterior o a la página de inicio si no hay una página anterior
        return "redirect:" + (referer != null ? referer : "/");
    }
}
