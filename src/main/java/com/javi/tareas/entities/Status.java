package com.javi.tareas.entities;

/**
 * Enumerado que representa los posibles estados de una tarea
 */
public enum Status {
    /**
     * Estado que indica que la tarea está pendiente
     */
    PENDING("Pendiente"),

    /**
     * Estado que indica que la tarea está en progreso
     */
    PROGRESS("En Proceso"),

    /**
     * Estado que indica que la tarea está completada
     */
    COMPLETED("Completado"),
    ;

    private final String text;

    /**
     * Constructor privado para asignar una etiqueta a cada estado
     *
     * @param text La etiqueta descriptiva del estado
     */
    Status(String text) {
        this.text = text;
    }

    /**
     * Obtiene la etiqueta descriptiva del estado
     *
     * @return La etiqueta del estado
     */
    public String getText() {
        return text;
    }
}
