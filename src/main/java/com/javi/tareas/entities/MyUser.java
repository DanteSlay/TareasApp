package com.javi.tareas.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Entity
@Table(name = "user-app")
public class MyUser {

    /**
     * ID único del usuario
     */
    @Id
    @GeneratedValue
    private long id;

    /**
     * El nombre del usuario. No debe estar en blanco
     */
    @NotBlank(message = "{myUser.nameBlank.error}")
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * La dirección de correo electrónico del usuario. Debe ser una dirección de correo electrónico válida.
     */
    @Column(nullable = false, unique = true)
    @Email
    private String email;

    private String password;

    private String role;

}
