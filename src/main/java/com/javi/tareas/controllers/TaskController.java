package com.javi.tareas.controllers;

import com.javi.tareas.component.SessionValidator;
import com.javi.tareas.entities.SearchFilter;
import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import com.javi.tareas.services.TaskServices;
import com.javi.tareas.utilities.Utilities;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Esta clase maneja las solicitudes relacionadas con la creación, visualización, edición, eliminación y ordenación de tareas.
 * Además, gestiona la selección de opciones de ordenación y el cambio de estado de las tareas.
 */
@Slf4j // Anotación que habilita la funcionalidad de registro (logging) en la clase.
@Controller // Marca esta clase como un controlador de Spring MVC.
public class TaskController {

    @Autowired
    private SessionValidator sessionValidator;

    @Autowired // Inyecta una instancia del bean TaskServices
    private TaskServices taskService;

    /**
     * Variable que guarda el ID del usuario que entra en este controlador
     */
    private Long userId;


    private static final int COOKIE_MAX_RANGE = 604800;


    /**
     * Método privado que se encarga de ordenar una lista de tareas y agregarla a un modelo, según la opción de ordenación especificada.
     *
     * @param sortOption La opción de ordenación que determina cómo se ordenarán las tareas.
     * @param model      El modelo al que se añadirá la lista de tareas ordenadas.
     */
    private void sortAndAddToModel(String sortOption, List<Task> taskList, Model model) {
        switch (sortOption) {
            case "title" -> model.addAttribute("taskList", taskService.sortByTitle(taskList));
            case "date" -> model.addAttribute("taskList", taskService.sortByDate(taskList));
            case "status" -> model.addAttribute("taskList", taskService.sortByStatus(taskList));
        }
    }


    /**
     * Método controlador que se encarga de mostrar la página principal de tareas para un usuario específico.
     *
     * @param id         El ID del usuario para el cual se mostrarán las tareas.
     * @param sortOption Cookie que almacena la opción de ordenación
     * @param model      El modelo utilizado para pasar datos a la vista.
     * @return La vista "index" que muestra las tareas del usuario.
     */
    @GetMapping("/home/{id}")
    public String home(@PathVariable("id") Long id, @CookieValue(name = "sortOption", defaultValue = "status") String sortOption
            , @RequestParam(name = "search", required = false) String searchTitle
            , HttpSession session
            , HttpServletRequest request
            , Model model) {
        // Establece el ID del usuario actual y lo guardamos en una cookie

        if (sessionValidator.isValidUserSession(session, id)) {
            userId = id;
            Cookie[] cookies = request.getCookies();
            boolean searchCookiesExist = false;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().startsWith("search")) {
                        searchCookiesExist = true;
                        break;
                    }
                }
            }

            if (searchCookiesExist) {
                SearchFilter searchFilter = SearchFilter.searchFilterFromCookies(Arrays.asList(cookies));

                model.addAttribute("searchFilter", searchFilter);
                model.addAttribute("statusList", searchFilter.getStatusList());
                List<Task> applyFilters = taskService.applyFilters(searchFilter, userId);
                sortAndAddToModel(sortOption, applyFilters, model);
                model.addAttribute("sortOption", sortOption);
                model.addAttribute("filters", true);

            } else {
                model.addAttribute("searchFilter", new SearchFilter());
                sortAndAddToModel(sortOption, taskService.findAll(userId), model);
                model.addAttribute("sortOption", sortOption);
                model.addAttribute("filters", false);
            }

            return "/taskHome/index";
        } else {
            return "/logIn/index";
        }

    }

    /**
     * Método controlador que genera una nueva tarea con los siguientes atributos predeterminados:
     * - ID de usuario: el ID de usuario con el que se inició la vista.
     * - Fecha de vencimiento: la fecha actual.
     * - Hora de vencimiento: la hora actual.
     * Muestra la página para añadir una nueva tarea.
     *
     * @param model El modelo utilizado para pasar datos a la vista.
     * @return La vista "new-task" que muestra un formulario para añadir la nueva tarea.
     */
    @GetMapping("/newTask")
    public String newTask(HttpSession session, Model model) {
        if (sessionValidator.isValidUserSession(session, userId)) {

            Task task = Task.builder()
                    .dueDate(LocalDate.now())
                    .time(LocalTime.now())
                    .build();
            model.addAttribute("taskDt", task);
            return "taskHome/new-task";
        }
        return "logIn/index";
    }

    /**
     * Método controlador que procesa el formulario para crear y agregar una nueva tarea.
     * Realiza las siguientes acciones:
     * - Valida la tarea proporcionada mediante anotaciones de validación.
     * - Si se encuentran errores de validación, regresa a la vista "new-task".
     * - Si la tarea no tiene un horario definido y se intenta guardar, se rechaza con un error de tiempo.
     * - Si la validación es exitosa, agrega la tarea al repositorio y redirige al usuario a su página de inicio.
     *
     * @param newTask La tarea proporcionada a través del formulario y anotada como válida.
     * @param result  El resultado de la validación que se utilizará para detectar errores.
     * @return Si hay errores de validación, retorna la vista "new-task". Si la tarea se guarda exitosamente,
     * redirige al usuario a su página de inicio.
     */
    @PostMapping("/newTask/submit")
    public String newTask(@Valid @ModelAttribute("taskDt") Task newTask,
                          BindingResult result) {
        if (result.hasErrors()) {
            return "new-task";
        } else {
            if (taskService.timeNullValid(newTask)) {
                result.rejectValue("time", "time.error");
                return "new-task";
            }
        }

        // Si la opción "Todo el día" está marcada, se le asigna un valor nulo al tiempo.
        if (newTask.getStatus() == null) newTask.setStatus(Status.PENDING);
        if (newTask.getAllDay()) newTask.setTime(null);

        taskService.add(newTask, userId);
        return "redirect:/home/" + userId;
    }

    /**
     * Método controlador que muestra los detalles de una tarea específica.
     *
     * @param id    El ID de la tarea que se desea ver.
     * @param model El modelo utilizado para pasar datos a la vista.
     * @return La vista "task" que muestra los detalles de la tarea.
     */
    @GetMapping("/viewTask")
    public String viewTask(@RequestParam("id") Long id, HttpSession session, Model model) {
        if (sessionValidator.isValidUserSession(session, userId)) {
            model.addAttribute("taskDt", taskService.find(id));
            return "taskHome/task";
        }
        return "logIn/index";
    }

    /**
     * Método controlador que elimina una tarea específica del repositorio de tareas.
     *
     * @param idTask El ID de la tarea que se desea eliminar.
     * @return Redirige al usuario a su página de inicio después de eliminar la tarea.
     */
    @GetMapping("/deleteModal")
    public String deleteModal(@RequestParam("id") Long idTask, HttpSession session, Model model) {
        if (sessionValidator.isValidUserSession(session, userId)) {
            Task t = taskService.find(idTask);
            model.addAttribute("taskDelete", t);
            return "fragments/deleteModal";
        }
        return "logIn/index";

    }

    @GetMapping("/deleteTask")
    public String delete(@RequestParam("task") Long idTask, HttpSession session) {
        if (sessionValidator.isValidUserSession(session, userId)) {
            taskService.delete(idTask);
            return "redirect:/home/" + userId;
        }
        return "logIn/index";
    }
    /**
     * Método controlador que permite la edición de una tarea específica.
     *
     * @param idTask El ID de la tarea que se desea editar.
     * @param model  El modelo utilizado para pasar datos a la vista de edición.
     * @return La vista "edit-task", un formulario con los detalles de la tarea que se desea editar.
     */
    @GetMapping("/editTask")
    public String edit(@RequestParam("idTask") Long idTask, HttpSession session, Model model) {
        if (sessionValidator.isValidUserSession(session, userId)) {
            Task t = taskService.find(idTask);
            model.addAttribute("taskDt", t);
            return "taskHome/edit-task";
        } else {
            return "logIn/index";
        }
    }

    /**
     * Método controlador que procesa el formulario de edición de una tarea y actualiza la tarea correspondiente.
     * Realiza las siguientes acciones:
     * - Valida la tarea editada mediante anotaciones de validación.
     * - Si se encuentran errores de validación, regresa a la vista "edit-task" para corregirlos.
     * - Si la tarea editada no tiene un horario definido y se intenta guardar, se rechaza con un error de tiempo.
     * - Si la validación es exitosa, actualiza la tarea en el repositorio y redirige al usuario a la vista de detalles de la tarea.
     *
     * @param editTask La tarea editada proporcionada a través del formulario y anotada como válida.
     * @param result   El resultado de la validación que se utilizará para detectar errores.
     * @return Si hay errores de validación, retorna la vista "edit-task". Si la tarea se actualiza exitosamente,
     * redirige al usuario a la vista de detalles de la tarea.
     */
    @PostMapping("/editTask/submit")
    public String updateTask(@Valid @ModelAttribute("taskDt") Task editTask, BindingResult result) {
        if (result.hasErrors()) {
            return "taskHome/edit-task";
        } else {
            if (taskService.timeNullValid(editTask)) {
                result.rejectValue("time", "time.error");
                return "taskHome/edit-task";
            }
        }

        taskService.updateTask(editTask);
        return "redirect:/viewTask?id=" + editTask.getId();
    }

    /**
     * Método controlador que actualiza el estado de una tarea existente en el repositorio y redirige al usuario a su página de inicio.
     *
     * @param taskId    El ID de la tarea que se actualizará.
     * @param newStatus El nuevo estado que se asignará a la tarea.
     * @return Redirige al usuario a su página de inicio después de la actualización.
     */
    @PostMapping("/home/updateStatus/{id}")
    public String updateTaskStatus(@PathVariable("id") Long taskId, @RequestParam("status") Status newStatus) {
        taskService.updateStatus(taskId, newStatus);
        return "redirect:/home/" + userId;
    }

    /**
     * Método controlador que permite al usuario ordenar las tareas en su página de inicio según una opción seleccionada.
     *
     * @param sortOption La opción de ordenación seleccionada por el usuario.
     * @param response   respuesta HTTP para generar una cookie con la elección de ordenación del usuario
     * @return Redirige al usuario a su página de inicio con las tareas ordenadas.
     */
    @GetMapping("/home/sortBy")
    public String sortBy(@RequestParam("sortOption") String sortOption, HttpServletResponse response) {
        if (sortOption == null) {
            sortOption = "status";
        }


        Cookie cookieSort = new Cookie("sortOption", sortOption);
        cookieSort.setMaxAge(COOKIE_MAX_RANGE);
        cookieSort.setPath("/home/");

        response.addCookie(cookieSort);

        return "redirect:/home/" + userId;
    }

    /**
     * Maneja la solicitud POST para aplicar filtros de búsqueda y establecer cookies correspondientes.
     *
     * @param searchFilter El objeto `SearchFilter` con los filtros de búsqueda del usuario.
     * @param request      El objeto `HttpServletRequest` para acceder a las cookies actuales.
     * @param response     El objeto `HttpServletResponse` para establecer las cookies de búsqueda.
     * @return Redirección a la página de inicio de usuario después de aplicar los filtros de búsqueda.
     */
    @PostMapping("/home/searchFilter/submit")
    public String searchFilter(@ModelAttribute("searchFilter") SearchFilter searchFilter, HttpServletRequest request, HttpServletResponse response) {
        Utilities.deleteCookiesByTitleSubstring(request, response, "search");

        HashMap<String, String> cookiesSerach = searchFilter.cookiesSerach();
        for (Map.Entry<String, String> cookie : cookiesSerach.entrySet()) {
            Cookie cookieFound = new Cookie(cookie.getKey(), cookie.getValue());
            cookieFound.setMaxAge(COOKIE_MAX_RANGE);
            cookieFound.setPath("/home");
            response.addCookie(cookieFound);
        }
        return "redirect:/home/" + userId;
    }

    /**
     * Maneja la solicitud GET para eliminar los filtros de búsqueda y las cookies relacionadas.
     *
     * @param request  El objeto `HttpServletRequest` para acceder a las cookies actuales.
     * @param response El objeto `HttpServletResponse` para eliminar las cookies de búsqueda.
     * @return Redirección a la página de inicio de usuario después de eliminar los filtros de búsqueda.
     */
    @GetMapping("/home/deleteFilters")
    public String deleteFilters(HttpServletRequest request, HttpServletResponse response) {
        Utilities.deleteCookiesByTitleSubstring(request, response, "search");
        return "redirect:/home/" + userId;
    }
}
