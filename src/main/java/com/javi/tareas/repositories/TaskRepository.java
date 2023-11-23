package com.javi.tareas.repositories;

import com.javi.tareas.entities.Task;
import com.javi.tareas.entities.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interfaz de repositorio Spring Data JPA para la clase de entidad Task.
 * Extiende la interfaz JpaRepository, que proporciona operaciones básicas de CRUD y algunos métodos adicionales para trabajar con entidades JPA.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByMyUser(MyUser myUser);
}
