package com.javi.tareas.entities;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @NotNull
    @Size(min=1, max = 50, message = "{Title.error}")
    private String title;

    private String description;

    @NotNull
    @FutureOrPresent(message = "{dueDate.error}")
    private LocalDate dueDate;

    private Status status;
}
