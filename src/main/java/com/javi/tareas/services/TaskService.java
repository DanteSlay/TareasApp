package com.javi.tareas.services;

import com.javi.tareas.entities.FiltrosTask;
import com.javi.tareas.entities.MyUser;
import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import com.javi.tareas.repositories.TaskRepository;
import com.javi.tareas.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar tareas en el sistema
 */
@Service
@Slf4j
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;


    /**
     * Busca y devuelve todas las tareas que están asociadas al mismo ID de usuario
     */
    public List<Task> findAll(MyUser user) {
        return taskRepository.findAllByMyUser(user);
    }

    /**
     * Guarda una tarea
     */
    public Task save(Task t) {
        return taskRepository.save(t);
    }

    /**
     * Busca una tarea por su ID
     */
    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    /**
     * Elimina una tarea
     */
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    /**
     * Busca una tarea por su ID y actualiza su estado
     */
    public void updateStatus(Long id, Status status) {
        Task t = findById(id);
        t.setStatus(status);
        taskRepository.save(t);
    }

    /**
     * Verifica si una tarea tiene el atributo 'allDay' establecido en 'false' y su campo 'time' está vacío.
     */
    public boolean timeNullValid(Task task) {
        return !task.getAllDay() && task.getTime() == null;
    }

    /**
     * Devuelve una lista de tareas ordenadas alfabéticamente por el título.
     */
    public List<Task> sortByTitle(List<Task> taskList) {
        taskList.sort(Comparator.comparing(Task::getTitle));
        return taskList;
    }

    /**
     * Devuelve una lista de tareas ordenadas por su fecha y hora.
     * Las tareas sin una hora definida se ordenan primero.
     */
    public List<Task> sortByDate(List<Task> taskList) {
        taskList.sort(
                Comparator.comparing(Task::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(Task::getTime, Comparator.nullsFirst(Comparator.naturalOrder()))
        );
        return taskList;
    }


    /**
     * Devuelve una lista de tareas ordenadas por su estado
     */
    public List<Task> sortByStatus(List<Task> taskList) {
        taskList.sort(Comparator.comparing(Task::getStatus));
        return taskList;
    }

    /**
     * Devuelve una lista de tareas filtradas de un Usuario
     */
    public List<Task> applyFilters(FiltrosTask filtrosTask, MyUser user) {
        List<Task> taskList = findAll(user);

        if (filtrosTask.getTitle() != null && !filtrosTask.getTitle().isEmpty()) {
            taskList = taskList.stream().filter(task -> task.getTitle().contains(filtrosTask.getTitle())).collect(Collectors.toList());
        }

        if (filtrosTask.getStartDueDate() != null) {
            taskList = taskList.stream().filter(task -> task.getDueDate().isAfter(filtrosTask.getStartDueDate()) || task.getDueDate().equals(filtrosTask.getStartDueDate())).collect(Collectors.toList());
        }

        if (filtrosTask.getEndDueDate() != null) {
            taskList = taskList.stream().filter(task -> task.getDueDate().isBefore(filtrosTask.getEndDueDate()) || task.getDueDate().equals(filtrosTask.getEndDueDate())).collect(Collectors.toList());
        }

        if (filtrosTask.getStatusList() != null && !filtrosTask.getStatusList().isEmpty()) {
            for (Status s : filtrosTask.getStatusList()) {
                if (s.equals(Status.PENDING))
                    taskList = taskList.stream().filter(task -> task.getStatus().equals(s)).collect(Collectors.toList());
                if (s.equals(Status.PROGRESS))
                    taskList = taskList.stream().filter(task -> task.getStatus().equals(s)).collect(Collectors.toList());
                if (s.equals(Status.COMPLETED))
                    taskList = taskList.stream().filter(task -> task.getStatus().equals(s)).collect(Collectors.toList());
            }
        }
        return taskList;
    }
}
