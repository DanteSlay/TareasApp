package com.javi.tareas.entities;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
public class Task {
    private static long lastId = 1;
    private long id;

    @NotEmpty(message = "{title.error.empty}")
    @Size(max = 50, message = "{title.error}")
    private String title;

    private String description;

    @NotNull(message = "{dueDate.null}")
    @FutureOrPresent(message = "{dueDate.error}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

    private Boolean allDay;

    private Status status;

    public Task() {
        this.id = ++lastId;
    }
}
