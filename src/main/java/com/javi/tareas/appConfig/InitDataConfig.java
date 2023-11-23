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
import java.util.List;

@Configuration
@AllArgsConstructor
public class InitDataConfig {
    private final TaskService taskService;
    private final UserService userService;


    /**
     * Cargamos los datos iniciales.
     * Si el Usuario con ID 1 no existe guarda uno predeterminado en la base de datos.
     * Busca si el Usuario predeterminado tiene alguna Tarea en caso contrario crea una y se la asigna al Usuario predeterminado.
     */
    @PostConstruct
    public void initTask() {
        MyUser myUserPredeterminado = userService.findById(1L);
        if (myUserPredeterminado == null) {
            myUserPredeterminado = MyUser.builder()
                    .username("admin")
                    .email("admin@gmail.com")
                    .password("root1234")
                    .role("ROLE_ADMIN")
                    .build();
            userService.save(myUserPredeterminado);
        }

        List<Task> tasksUserPredeterminado = taskService.findAll(myUserPredeterminado);
        if (tasksUserPredeterminado.isEmpty()) {
            Task tPredeterminada = Task.builder()
                    .title("Tarea 1")
                    .description("Primera Tarea")
                    .dueDate(LocalDate.of(2022, 5, 14))
                    .allDay(true)
                    .status(Status.PROGRESS)
                    .myUser(userService.findById(1L))
                    .build();
            taskService.save(tPredeterminada);
        }

    }
}
