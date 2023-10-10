package com.javi.tareas.entities;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa un usuario en el sistema
 */
@Data //Genera métodos getters, setters, toString... al objeto
@Builder // Genera un patron de diseño para que la construcción del objeto sea más legible
@AllArgsConstructor // Genera un constructor con todos los campos inicializados
@NoArgsConstructor // Genera un constructor sin proporcionar valores iniciales para sus campos
public class User {
    private static long lastId = 1; // Se inicializa en 1 puesto que se creará un usuario al iniciar la aplicación con este valor

    /**
     * ID único del usuario
     */
    private long id;

    /**
     * El nombre del usuario. No debe estar en blanco
     */
    @NotBlank(message = "{user.nameBlank.error}")
    private String username;

    /**
     * La dirección de correo electrónico del usuario. Debe ser una dirección de correo electrónico válida.
     */
    @Email(message = "{user.email.error}")
    private String email;

    /**
     * La contraseña del usuario. Debe tener entre 8 y 20 caracteres de longitud.
     */
    @Size(min = 8, max = 20)
    private String password;

    /**
     * Genera un ID único para el usuario proporcionado y lo asigna como su ID.
     *
     * @param user El usuario al que se le asignará el ID generado.
     * @return El usuario con su ID único asignado.
     */
    public User generateId(User user) {
        user.setId(++lastId);
        return user;
    }
}
