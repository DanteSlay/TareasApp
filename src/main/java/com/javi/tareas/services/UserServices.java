package com.javi.tareas.services;

import com.javi.tareas.entities.User;
import com.javi.tareas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa un servicio para gestionar usuarios en el sistema
 */
@Service // Marcamos esta clase como un componente de servicio
@RequiredArgsConstructor
public class UserServices {
    private final UserRepository userRepository;

    /**
     * Busca y devuelve un Usuario mediante su clave
     *
     * @param email La clave del repositorio para encontrar a un Usuario
     * @return El usuario encontrado
     */
    public User get(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Se utiliza para añadir un usuario en el repositorio de usuarios, el cual previamente se ha generado un ID único
     *
     * @param newUser El nuevo usuario que se añadirá al repositorio
     * @return El usuario con su ID generado y agregado al repositorio de usuarios
     */
    public User add(User newUser) {
        userRepository.save(newUser);
        return newUser;
    }

    /**
     * Verifica si el Email proporcionado ya existe en el repositorio de usuarios
     *
     * @param email El email que se desea verificar su existencia.
     * @return Un valor booleano que indica si el email ya existe (true) o no (false)
     */
    public boolean emailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

    /**
     * Verifica si la contraseña proporcionada se corresponde con la contraseña del usuario proporcionado
     *
     * @param email El usuario a partir del cual se comprobará la contraseña
     * @param password La contraseña que se desea verificar
     * @return Un valor booleano que indica si la contraseña se corresponde con la del usuario (true) o no (false)
     */
    public boolean passwordCorrect(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password) != null;
    }

    /**
     * Verifica si un Email y una contraseña proporcionados se corresponden con los de algún usuario en el repositorio de usuarios
     *
     * @param email El email que se desea verificar
     * @param password La contraseña que se desea verificar
     * @return Un valor booleano que indica si los datos corresponden a algún usuario del repositorio (true) o no (false)
     */
    public boolean authenticationSuccess(String email, String password) {
        return passwordCorrect(email, password);
    }

    /**
     *Verifica si un nombre de usuario ya existe en el repositorio de usuarios
     *
     * @param username El nombre de usuario que se desea verificar
     * @return Un valor booleano que devuelve si el nombre de usuario ya existe (true) o no (false)
     */
    public boolean usernameExist(String username) {
        return userRepository.findByUsername(username) != null;
    }

    /**
     * Valida un nuevo usuario y una contraseña repetida proporcionados
     * Verificando que:
     * - La contraseña del nuevo usuario y la contraseña repetida sean la misma
     * - El nombre del nuevo usuario no exista previamente en el repositorio de usuarios
     * - El email del nuevo usuario no exista previamente en el repositorio de usuarios
     * Devolviendo un mapa con los errores correspondientes a cada verificación
     *
     * @param newUser El nuevo usuario que se desea verificar
     * @param repeatPassword La contraseña repetida del nuevo usuario para verificar que ambas son iguales
     * @return Un mapa de errores, con su mensaje de error, de las verificaciones que no se pudieron realizar
     */
    public Map<String, String> newUserValidation(User newUser, String repeatPassword) {

        Map<String, String> errors = new HashMap<>();

        if (!newUser.getPassword().equals(repeatPassword)) errors.put("password", "user.password2.error");

        if (usernameExist(newUser.getUsername())) errors.put("username", "user.nameExist.error");

        if (emailExist(newUser.getEmail())) errors.put("email", "user.emailExist.error");

        return errors;
    }
}
