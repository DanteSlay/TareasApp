package com.javi.tareas.entities;

public enum Status {
    PENDING("Pendiente"),
    PROGRESS("En Proceso"),
    COMPLETED("Completado");

    private final String text;

    Status(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
