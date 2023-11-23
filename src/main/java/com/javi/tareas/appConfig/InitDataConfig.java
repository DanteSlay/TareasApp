package com.javi.tareas.appConfig;

import com.javi.tareas.entities.MyUser;
import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import com.javi.tareas.services.TaskService;
import com.javi.tareas.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;


import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@AllArgsConstructor
public class InitDataConfig {
    private final TaskService taskService;
    private final UserService userService;


    /**
     * Cargamos los datos iniciales.
     * Si el usuario con ID 1 no existe guarda uno predeterminado en la base de datos.
     * Busca si la tarea con ID 1 existe, en caso contrario crea una y se la asigna al usuario predeterminado.
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
