package com.javi.tareas.entities;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @NotNull
    @Size(min=1, max = 50, message = "{Title.error}")
    private String title;

    private String description;

    @NotNull(message = "{dueDate.null}")
    @FutureOrPresent(message = "{dueDate.error}")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dueDate;

    private LocalTime time;

    private Boolean allDay;

    private Status status;
}
