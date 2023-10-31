package com.javi.tareas.services;

import com.javi.tareas.entities.SearchFilter;
import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Clase que representa un servicio para gestionar tareas en el sistema
 */
@Service // Marcamos esta clase como un componente de servicio
@Slf4j // Usamos esta anotación para poder incluir "logs" y registrar mensajes de depuración
public class TaskServices {
    private HashMap<Long, Task> taskRepository = new HashMap<>();

    /**
     * Método de iniciación que se ejecutará después de la construcción del bean TaskServices
     * creando una instancia de Task con valores predefinidos
     * y las agrega al repositorio de tareas 'taskRepository'
     * Se utiliza para configurar datos iniciales de tareas cuando se inicia la aplicación.
     */
    @PostConstruct
    public void init() {
            Task t = Task.builder()
                    .id(1)
                    .title("Tarea 1")
                    .description("Primera Tarea")
                    .dueDate(LocalDate.of(2022, 5, 14))
                    .allDay(true)
                    .status(Status.PROGRESS)
                    .idUser(1)
                    .build();

        taskRepository.put(t.getId(), t);

    }

    /**
     * Busca y devuelve todas las tareas en el repositorio que están asociadas al mismo ID de usuario
     *
     * @param idUsuario El ID del usuario cuyas tareas se desean buscar
     * @return Una lista de tareas asociadas al ID de usuario proporcionado
     */
    public List<Task> findAll(Long idUsuario) {
        return taskRepository.values().stream()
                .filter(task -> task.getIdUser() == idUsuario)
                .collect(Collectors.toList());
    }

    /**
     * Genera un ID único para la tarea proporcionada y la agrega al repositorio de tareas.
     *
     * @param t La tarea a la que se le va a generar y asignar un ID único.
     * @return La tarea con su ID generado y agregada al repositorio de tareas.
     */
    public Task add(Task t) {
        t.generateId(t);
        taskRepository.put(t.getId(), t);
        return t;
    }

    /**
     * Busca una tarea en el repositorio por su ID
     *
     * @param id El ID de la tarea que se desea buscar
     * @return La tarea cuyo ID coincide con el proporcionado
     */
    public Task find(Long id) {
        return taskRepository.get(id);
    }

    /**
     * Elimina la tarea del repositorio por su ID
     * @param id El ID de la tarea que se desea eliminar
     */
    public void delete(Long id) {
        taskRepository.remove(id);
    }

    /**
     * Busca una tarea por su ID y actualiza su estado por el nuevo estado proporcionado
     *
     * @param id El ID de la tarea que se desea editar
     * @param status El nuevo estado que se le asignara a la tarea
     */
    public void updateStatus(Long id, Status status) {
        find(id).setStatus(status);
    }

    /**
     * Buscamos una tarea por su ID y actualizamos todos sus atributos a partir de una nueva tarea proporcionada
     *
     * @param newTask La tarea con algunos o todos los campos actualizados
     * @return La tarea actualizada después de la actualización de sus atributos
     */
    public Task updateTask(Task newTask) {
        Long key = newTask.getId();
        Task updateTask = taskRepository.get(key);

        updateTask.setTitle(newTask.getTitle());
        updateTask.setDescription(newTask.getDescription());
        updateTask.setDueDate(newTask.getDueDate());
        updateTask.setAllDay(newTask.getAllDay());
        updateTask.setTime(newTask.getTime());
        updateTask.setStatus(newTask.getStatus());

        taskRepository.put(key, updateTask);
        return updateTask;
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
     * @param userId El ID del usuario al que pertenecen las tareas.
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
     * @param userId El ID del usuario al que pertenecen las tareas.
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
     * @param userId El ID del usuario al que pertenecen las tareas
     * @return Una lista de tareas ordenadas por su estado
     */
    public List<Task> sortByStatus(List<Task> taskList) {
        taskList.sort(Comparator.comparing(Task::getStatus));
        return taskList;
    }

    public List<Task> findTask(String titleSearch, Long idUser) {
        return findAll(idUser).stream()
                .filter(task -> task.getTitle().equals(titleSearch))
                .toList();
    }

    public List<Task> applyFilters(SearchFilter searchFilter, Long idUser) {
        List<Task> taskList = findAll(idUser);

        if (searchFilter.getTitle() != null && !searchFilter.getTitle().isEmpty()) {
            taskList = taskList.stream().filter(task -> task.getTitle().contains(searchFilter.getTitle())).collect(Collectors.toList());
        }

        if (searchFilter.getStartDueDate() != null) {
            taskList = taskList.stream().filter(task -> task.getDueDate().isAfter(searchFilter.getStartDueDate()) || task.getDueDate().equals(searchFilter.getStartDueDate())).collect(Collectors.toList());
        }

        if (searchFilter.getEndDueDate() != null) {
            taskList = taskList.stream().filter(task -> task.getDueDate().isBefore(searchFilter.getEndDueDate()) ||task.getDueDate().equals(searchFilter.getEndDueDate()) ).collect(Collectors.toList());
        }

        if (searchFilter.getStatusList() != null && !searchFilter.getStatusList().isEmpty()) {
            for (Status s : searchFilter.getStatusList()) {
                if (s.equals(Status.PENDING)) taskList = taskList.stream().filter(task -> task.getStatus().equals(s)).collect(Collectors.toList());
                if (s.equals(Status.PROGRESS)) taskList = taskList.stream().filter(task -> task.getStatus().equals(s)).collect(Collectors.toList());
                if (s.equals(Status.COMPLETED)) taskList = taskList.stream().filter(task -> task.getStatus().equals(s)).collect(Collectors.toList());
            }
        }
        return taskList;
    }
}
