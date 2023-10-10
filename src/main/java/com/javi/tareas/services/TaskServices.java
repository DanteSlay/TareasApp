package com.javi.tareas.services;

import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskServices {
    private HashMap<Long, Task> taskRepository = new HashMap<>();

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

    public List<Task> findAll(Long idUsuario) {
        return taskRepository.values().stream()
                .filter(task -> task.getIdUser() == idUsuario)
                .collect(Collectors.toList());
    }

    public Task add(Task t) {
        t.generateId(t);
        taskRepository.put(t.getId(), t);
        return t;
    }

    public Task find(Long id) {
        return taskRepository.get(id);
    }

    public void delete(Long id) {
        taskRepository.remove(id);
    }

    public void updateStatus(Long id, Status status) {
        find(id).setStatus(status);
    }

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

    public boolean timeNullValid(Task task) {
        return !task.getAllDay() && task.getTime() == null;
    }

    public List<Task> sortByTitle(Long userId) {
        List<Task> taskList = findAll(userId);
        taskList.sort(Comparator.comparing(Task::getTitle));
        return taskList;
    }

    public List<Task> sortByDate(Long userId) {
        List<Task> taskList = findAll(userId);
        taskList.sort(
                Comparator.comparing(Task::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(Task::getTime, Comparator.nullsFirst(Comparator.naturalOrder()))
        );
        return taskList;
    }


    public List<Task> sortByStatus(Long userId) {
        List<Task> taskList = findAll(userId);
        taskList.sort(Comparator.comparing(Task::getStatus));
        return taskList;
    }
}
