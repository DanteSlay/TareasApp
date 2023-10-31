package com.javi.tareas.entities;

import jakarta.servlet.http.Cookie;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Clase que representa un filtro de búsqueda.
 */
@Data
@Slf4j
public class SearchFilter {

    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDueDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDueDate;

    private List<Status> statusList;

    /**
     * Constructor de la clase SearchFilter. Inicializa la lista de estados.
     */
    public SearchFilter() {
        this.statusList = new ArrayList<>();
    }

    /**
     * Convierte los criterios de búsqueda en cookies para mantener el estado del filtro.
     *
     * @return Un mapa de cookies con los criterios de búsqueda.
     */
    public HashMap<String, String> cookiesSerach() {
        HashMap<String, String> cookies = new HashMap<>();

        if (title != null && !title.isEmpty()) cookies.put("searchTitle", title);

        if (startDueDate != null) cookies.put("searchStartDate", startDueDate.toString());

        if (endDueDate != null) cookies.put("searchEndDate", endDueDate.toString());

        if (statusList != null) {
            for (Status status : statusList) {
                if (status.equals(Status.PENDING)) cookies.put("searchPending", status.toString());
                if (status.equals(Status.PROGRESS)) cookies.put("searchProgress", status.toString());
                if (status.equals(Status.COMPLETED)) cookies.put("searchCompleted", status.toString());
            }
        }
        return cookies;

    }

    /**
     * Crea un objeto SearchFilter a partir de cookies almacenadas previamente.
     *
     * @param cookies Lista de cookies con los criterios de búsqueda.
     * @return Un objeto SearchFilter con los criterios recuperados de las cookies.
     */
    public static SearchFilter searchFilterFromCookies(List<Cookie> cookies) {
        SearchFilter searchFilter = new SearchFilter();
        for (Cookie cookie : cookies) {
            switch (cookie.getName()) {
                case "searchTitle" -> searchFilter.setTitle(cookie.getValue());
                case "searchStartDate" -> searchFilter.setStartDueDate(LocalDate.parse(cookie.getValue()));
                case "searchEndDate" -> searchFilter.setEndDueDate(LocalDate.parse(cookie.getValue()));
                case "searchPending" -> searchFilter.statusList.add(Status.PENDING);
                case "searchProgress" -> searchFilter.statusList.add(Status.PROGRESS);
                case "searchCompleted" -> searchFilter.statusList.add(Status.COMPLETED);
            }
        }
        return searchFilter;
    }
}
