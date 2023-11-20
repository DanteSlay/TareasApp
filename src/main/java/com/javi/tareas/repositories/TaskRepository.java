package com.javi.tareas.repositories;

import com.javi.tareas.entities.Task;
import com.javi.tareas.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUser(User user);

    @Query("SELECT MAX (t.id) from Task t")
    Long lastFindId();
}
