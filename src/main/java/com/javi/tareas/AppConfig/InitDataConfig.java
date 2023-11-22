package com.javi.tareas.AppConfig;

import com.javi.tareas.entities.MyUser;
import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import com.javi.tareas.services.TaskService;
import com.javi.tareas.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class InitDataConfig {
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;


    /**
     * Método de iniciación que se ejecutará después de la construcción del bean TaskServices
     * creando una instancia de Task con valores predefinidos
     * y las agrega al repositorio de tareas 'taskRepository'
     * Se utiliza para configurar datos iniciales de tareas cuando se inicia la aplicación.
     */
    @PostConstruct
    public void initTask() {
        MyUser myUser = userService.findById(1L);
        if (myUser == null) {
            myUser = MyUser.builder()
                    .username("admin")
                    .email("admin@gmail.com")
                    .password("root1234")
                    .role("ROLE_ADMIN")
                    .build();
            userService.save(myUser);
        }
        Task t = taskService.findById(1L);
        if (t == null) {
            t = Task.builder()
                    .title("Tarea 1")
                    .description("Primera Tarea")
                    .dueDate(LocalDate.of(2022, 5, 14))
                    .allDay(true)
                    .status(Status.PROGRESS)
                    .myUser(userService.findById(1L))
                    .build();
            taskService.save(t);
        }

    }
}
