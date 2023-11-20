package com.javi.tareas.component;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

/**
 * Clase para validar la sesión de usuario.
 */
@Component
public class SessionValidator {

    /**
     * Verifica si la sesión de usuario es válida.
     *
     * @param session Objeto HttpSession para acceder a la sesión del usuario.
     * @param idUser Identificador de usuario a comparar con el valor de la sesión.
     * @return true si la sesión es válida, false en caso contrario.
     */
    public boolean isValidUserSession(HttpSession session, Long idUser) {
        Object sessionUser = session.getAttribute("user");
        return sessionUser != null && sessionUser.equals(idUser);
    }
}
