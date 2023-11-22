package com.javi.tareas.services;

import com.javi.tareas.entities.FiltrosTask;
import com.javi.tareas.entities.MyUser;
import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import com.javi.tareas.repositories.TaskRepository;
import com.javi.tareas.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase que representa un servicio para gestionar tareas en el sistema
 */
@Service // Marcamos esta clase como un componente de servicio
@Slf4j // Usamos esta anotación para poder incluir "logs" y registrar mensajes de depuración
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;


    /**
     * Busca y devuelve todas las tareas en el repositorio que están asociadas al mismo ID de usuario
     *
     * @return Una lista de tareas asociadas al ID de usuario proporcionado
     */
    public List<Task> findAll(MyUser user) {
        return taskRepository.findAllByMyUser(user);
    }

    public Task save(Task t) {
        return taskRepository.save(t);
    }

    /**
     * Busca una tarea en el repositorio por su ID
     *
     * @param id El ID de la tarea que se desea buscar
     * @return La tarea cuyo ID coincide con el proporcionado
     */
    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    /**
     * Elimina la tarea del repositorio por su ID
     *
     * @param id El ID de la tarea que se desea eliminar
     */
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    /**
     * Busca una tarea por su ID y actualiza su estado por el nuevo estado proporcionado
     *
     * @param id     El ID de la tarea que se desea editar
     * @param status El nuevo estado que se le asignara a la tarea
     */
    public void updateStatus(Long id, Status status) {
        Task t = findById(id);
        t.setStatus(status);
        taskRepository.save(t);
    }

    /**
     * Verifica si una tarea proporcionada tiene el atributo 'allDay' establecido en 'false' y su campo 'time' está vacío.
     *
     * @param task La tarea que se desea validar.
     * @return Un valor booleano que indica si la validación es correcta (true) o no (false).
     */
    public boolean timeNullValid(Task task) {
        return !task.getAllDay() && task.getTime() == null;
    }

    /**
     * Selecciona todas las tareas del repositorio que coincidan con el ID del usuario proporcionado y devuelve una lista de tareas ordenadas alfabéticamente por el título
     *
     * @return Una lista de tareas ordenadas alfabéticamente por su título.
     */
    public List<Task> sortByTitle(List<Task> taskList) {
        taskList.sort(Comparator.comparing(Task::getTitle));
        return taskList;
    }

    /**
     * Selecciona todas las tareas del repositorio que coincidan con el ID del usuario proporcionado y devuelve una lista de tareas ordenadas por su fecha y hora
     * Las tareas sin una hora definida se ordenan primero.
     *
     * @return Una lista de tareas ordenadas por fecha y hora, con las tareas sin hora definida al principio.
     */
    public List<Task> sortByDate(List<Task> taskList) {
        taskList.sort(
                Comparator.comparing(Task::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(Task::getTime, Comparator.nullsFirst(Comparator.naturalOrder()))
        );
        return taskList;
    }


    /**
     * Selecciona todas las tareas del repositorio que coincidan con el ID del usuario proporcionado y devuelve una lista de tareas ordenadas por su estado
     *
     * @return Una lista de tareas ordenadas por su estado
     */
    public List<Task> sortByStatus(List<Task> taskList) {
        taskList.sort(Comparator.comparing(Task::getStatus));
        return taskList;
    }

    public List<Task> findTask(String titleSearch, MyUser user) {
        return findAll(user).stream()
                .filter(task -> task.getTitle().equals(titleSearch))
                .toList();
    }

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
