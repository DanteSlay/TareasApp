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
    private HashMap<Long, Task> TaskRepository = new HashMap<>();

    @PostConstruct
    public void init() {
            Task t = Task.builder()
                    .id(1)
                    .title("Tarea 1")
                    .description("Primera Tarea")
                    .dueDate(LocalDate.of(2022, 5, 14))
                    .allDay(true)
                    .status(Status.PROGRESS)
                    .build();

        TaskRepository.put(t.getId(), t);

    }

    public List<Task> findAll() {
        return TaskRepository.entrySet().stream()
                .sorted(Comparator.comparingLong(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public Task add(Task t) {
        TaskRepository.put(t.getId(), t);
        return t;
    }

    public Task find(Long id) {
        return TaskRepository.get(id);
    }

    public void delete(Long id) {
        TaskRepository.remove(id);
    }




}
