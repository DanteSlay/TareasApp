package com.javi.tareas.repositories;

import com.javi.tareas.entities.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * interfaz de repositorio Spring Data JPA para la clase de entidad Task. Extiende la interfaz JpaRepository, que proporciona operaciones básicas de CRUD y algunos métodos adicionales para trabajar con entidades JPA.
 */
@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> {
    MyUser findByEmail(String email);
    MyUser findByEmailAndPassword(String email, String password);
    MyUser findByUsername(String username);
}
