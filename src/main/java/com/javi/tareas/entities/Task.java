package com.javi.tareas.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Clase que representa una tarea en el sistema
 */
@Data //Genera métodos getters, setters, toString... al objeto
@Builder // Genera un patron de diseño para que la construcción del objeto sea más legible
@AllArgsConstructor // Genera un constructor con todos los campos inicializados
@NoArgsConstructor // Genera un constructor sin proporcionar valores iniciales para sus campos
@Entity
public class Task {

    /**
     * El ID único de la tarea
     */
    @Id @GeneratedValue
    private long id;

    /**
     * El título de la tarea. No debe estar en blanco y su longitud máxima será de 50 caracteres
     */
    @NotEmpty(message = "{title.error.empty}")
    @Size(max = 50, message = "{title.error}")
    private String title;

    /**
     * La descripción de la tarea
     */
    private String description;

    /**
     * La fecha de vencimiento de la tarea.
     * No debera ser nulo, además deberá ser posterior o igual a la fecha actual
     * El patron de la fecha se establecerá en yyyy-MM-dd
     */
    @NotNull(message = "{dueDate.null}")
//    @FutureOrPresent(message = "{dueDate.error}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    /**
     * La hora de vencimiento de la tarea
     * El patron de la hora se establecerá en HH:mm
     */
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

    /**
     * Indica si la duración de la tarea es de todo el dia o por el contrario necesita una hora de finalización
     */
    private Boolean allDay;

    /**
     * Indica el estado actual de la tarea, que podrá ser Pendiente, En progreso o Completado
     */
    private Status status;

    /**
     * El ID del usuario al que pertenece la tarea
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private MyUser myUser;

}
