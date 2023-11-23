package com.javi.tareas.entities;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

/**
 * Clase que representa un filtro de búsqueda.
 */
@Data
@Slf4j
public class FiltrosTask {

    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDueDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDueDate;

    private List<Status> statusList;

    /**
     * Constructor de la clase SearchFilter. Inicializa la lista de estados.
     */
    public FiltrosTask() {
        this.statusList = new ArrayList<>();
    }

}
