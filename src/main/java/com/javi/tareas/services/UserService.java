package com.javi.tareas.services;

import com.javi.tareas.entities.MyUser;
import com.javi.tareas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para gestionar usuarios en el sistema
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     *Busca un usuario por su nombre
     */
    public MyUser findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Busca un usuario por su ID
     */
    public MyUser findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Guarda un usuario
     */
    public MyUser save(MyUser newMyUser) {
        newMyUser.setPassword(passwordEncoder.encode(newMyUser.getPassword()));
        return userRepository.save(newMyUser);
    }

    /**
     * Verifica si un usuario buscado por su Email ya existe en el sistema.
     */
    public boolean emailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }



    /**
     * Verifica si un usuario buscado por su Nombre ya existe en el sistema.
     */
    public boolean usernameExist(String username) {
        return userRepository.findByUsername(username) != null;
    }

    /**
     Este método valida un nuevo registro de usuario asegurándose de que:
     - La contraseña ingresada coincida con la contraseña repetida.
     - El nombre de usuario y el correo electrónico no estén ya registrados en el sistema.
     Devuelve un mapa con los errores encontrados durante estas verificaciones.
     */
    public Map<String, String> newUserValidation(MyUser newMyUser, String repeatPassword) {

        Map<String, String> errors = new HashMap<>();

        if (!newMyUser.getPassword().equals(repeatPassword)) errors.put("password", "myUser.password2.error");

        if (usernameExist(newMyUser.getUsername())) errors.put("username", "myUser.nameExist.error");

        if (emailExist(newMyUser.getEmail())) errors.put("email", "myUser.emailExist.error");

        return errors;
    }
}
