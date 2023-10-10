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
     * @param lang El idioma seleccionado por el usuario como parámetro de solicitud.
     * @param request La solicitud HTTP que contiene la información de la sesión actual.
     * @return Una redirección a la página actual o a la página de inicio, según corresponda.
     */
    @GetMapping("/changeLanguage")
    public String changeLanguage(@RequestParam("lang") String lang, HttpServletRequest request) {
        // Establece el idioma seleccionado por el usuario en la sesión actual
        request.getSession().setAttribute("lang", lang);

        // Obtiene la URL de la página anterior (Referer) para redireccionar allí
        String referer = request.getHeader("Referer");

        // Retorna una redirección a la página anterior o a la página de inicio si no hay una página anterior
        return "redirect:" + (referer != null ? referer : "/");
    }
}
