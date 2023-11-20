package com.javi.tareas.config;

import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import com.javi.tareas.entities.User;
import com.javi.tareas.repositories.TaskRepository;
import com.javi.tareas.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class InitDataConfig {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Método de iniciación que se ejecutará después de la construcción del bean TaskServices
     * creando una instancia de Task con valores predefinidos
     * y las agrega al repositorio de tareas 'taskRepository'
     * Se utiliza para configurar datos iniciales de tareas cuando se inicia la aplicación.
     */
    @PostConstruct
    public void initTask() {
        User user = userRepository.findById(1L).orElse(null);
        if (user == null) {
            user = User.builder()
                    .username("admin")
                    .email("admin@gmail.com")
                    .password("root1234")
                    .build();
            userRepository.save(user);
        }
        Task t = taskRepository.findById(1L).orElse(null);
        if (t == null){
            t = Task.builder()
                    .title("Tarea 1")
                    .description("Primera Tarea")
                    .dueDate(LocalDate.of(2022, 5, 14))
                    .allDay(true)
                    .status(Status.PROGRESS)
                    .user(user)
                    .build();
        }else{
            Long lastId = taskRepository.lastFindId();
            lastId += 1;
            t = Task.builder()
                    .title("Tarea " + lastId)
                    .description("Primera Tarea")
                    .dueDate(LocalDate.now())
                    .allDay(true)
                    .status(Status.PENDING)
                    .user(user)
                    .build();
        }


        taskRepository.save(t);

    }
}
