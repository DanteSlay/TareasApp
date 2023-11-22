package com.javi.tareas.utilities;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Utilities {

    /**
     * Elimina cookies cuyos nombres comienzan con una subcadena específica.
     *
     * @param request        Objeto HttpServletRequest que proporciona las cookies.
     * @param response       Objeto HttpServletResponse para agregar cookies eliminadas.
     */
    public static void deleteSearchCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().startsWith("search")) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    cookie.setPath("/home");
                    response.addCookie(cookie);
                }
            }
        }
    }


}
