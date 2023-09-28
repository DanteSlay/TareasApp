package com.javi.tareas.services;

import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TaskServices {
    private List<Task> repository = new ArrayList<>();

    @PostConstruct
    public void init() {
        repository.add(
            Task.builder()
                    .title("Tarea 1")
                    .description("Primera Tarea")
                    .dueDate(LocalDate.of(2022, 5, 14))
                    .allDay(true)
                    .status(Status.PROGRESS)
                    .build()
        );
    }

    public List<Task> findAll() {
        return repository;
    }

    public Task add(Task t) {
        repository.add(t);
        return t;
    }

    public Task find(String title) {
        return repository.stream()
                .filter(task -> title.equals(task.getTitle()))
                .findAny()
                .orElse(null);
    }

    public void delete(String title) {
        Task t = find(title);
        repository.remove(t);
    }




}
