package com.javi.tareas.controllers;

import com.javi.tareas.entities.FiltrosTask;
import com.javi.tareas.entities.MyUser;
import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import com.javi.tareas.services.TaskService;
import com.javi.tareas.services.UserService;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


/**
 * Controlador encargado de manejar las acciones relacionadas con la gestión de tareas en la aplicación.
 * Aquí se incluyen la creación, visualización, edición, eliminación y ordenación de tareas.
 * También gestiona la selección de opciones de ordenación y el cambio de estado de las tareas.
 */
@Slf4j // Anotación que habilita la funcionalidad de registro (logging) en la clase.
@Controller // Indica que esta clase es un controlador de Spring MVC
@RequiredArgsConstructor // Genera un constructor con todos los argumentos automáticamente.
@RequestMapping("/home") // Establece la ruta base para todas las solicitudes manejadas por este controlador.
public class TaskController {
    private final TaskService taskService;

    private final UserService userService;

    private static final int COOKIE_MAX_RANGE = 604800;

    /**
     * Maneja la página principal de tareas, mostrando tareas según filtros y opciones de orden.
     * Además, presenta la lista de tareas en la página de inicio.
     */
    @GetMapping("")
    public String home(@CookieValue(name = "sortOption", defaultValue = "status") String sortOption
            , HttpSession session
            , Model model) {

        List<Task> showTask;

        // Obtiene los filtros de tarea de la sesión
        FiltrosTask filtrosTask = (FiltrosTask) session.getAttribute("filtrosTask");
        if (filtrosTask == null) {
            // Si no hay filtros, se crea un nuevo objeto FiltrosTask y se muestran todas las tareas
            model.addAttribute("filtrosTask", new FiltrosTask());
            showTask = taskService.findAll(usuarioAutenticado());
        }else{
            // Si existen filtros, se aplican para mostrar las tareas filtradas
            model.addAttribute("filtrosTask", filtrosTask);
            model.addAttribute("statusList", filtrosTask.getStatusList());
            showTask = taskService.applyFilters(filtrosTask, usuarioAutenticado());
        }

        // Ordena la lista de tareas y la añade al modelo para mostrar en la vista
        sortListAndAddToModel(sortOption, showTask, model);

        // Añade la opción de orden actual al modelo para reflejarla en la vista
        model.addAttribute("sortOption", sortOption);

        return "taskHome/index";
    }

    /**
     * Muestra un formulario para agregar una nueva tarea con valores predeterminados.
     */
    @GetMapping("/newTask")
    public String newTask(Model model) {
            Task task = Task.builder()
                    .dueDate(LocalDate.now())
                    .time(LocalTime.now())
                    .build();
            model.addAttribute("taskDt", task);
            return "taskHome/new-task";
    }

    /**
     * Procesa el formulario para crear y agregar una nueva tarea al repositorio.
     * Valida la tarea proporcionada y realiza las siguientes acciones:
     * - Si hay errores de validación, vuelve a mostrar el formulario de nueva tarea.
     * - Si la tarea no tiene un horario definido y se intenta guardar, rechaza con un error de tiempo.
     * - Si la validación es exitosa, agrega la tarea al repositorio y redirige al usuario a su página de inicio.
     */
    @PostMapping("/newTask/submit")
    public String newTask(@Valid @ModelAttribute("taskDt") Task newTask,
                          BindingResult result) {
        // Verifica si hay errores de validación en la tarea proporcionada
        if (result.hasErrors()) {
            return "taskHome/new-task";
        } else {
            // Si la tarea no tiene un horario definido y se intenta guardar, rechaza con un error de tiempo
            if (taskService.timeNullValid(newTask)) {
                result.rejectValue("time", "time.error");
                return "taskHome/new-task";
            }
        }

        // Si la opción "Todo el día" está marcada, se le asigna un valor nulo al tiempo.
        if (newTask.getStatus() == null) newTask.setStatus(Status.PENDING);
        if (newTask.getAllDay()) newTask.setTime(null);

        // Establece el usuario autenticado como propietario de la tarea y la guarda en el repositorio
        newTask.setMyUser(usuarioAutenticado());
        taskService.save(newTask);

        return "redirect:/home";
    }

    /**
     * Muestra los detalles de una tarea.
     */
    @GetMapping("/viewTask")
    public String viewTask(@RequestParam("id") Long id, Model model) {
            model.addAttribute("taskDt", taskService.findById(id));
            return "fragments/viewTaskModal";
    }

    /**
     * Proporciona a la vista el modal que confirma la eliminacion de una tarea.
    */
    @GetMapping("/deleteModal")
    public String deleteModal(@RequestParam("id") Long idTask, Model model) {
        Task t = taskService.findById(idTask);
        model.addAttribute("taskDelete", t);
        return "fragments/deleteModal";

    }

    /**
     *Elimina una tarea
     */
    @GetMapping("/deleteTask")
    public String delete(@RequestParam("task") Long idTask) {
            taskService.delete(idTask);
            return "redirect:/home";
    }

    /**
     * Muestra un formulario con los datos de una tarea para editarlos
     */
    @GetMapping("/editTask")
    public String edit(@RequestParam("idTask") Long idTask, Model model) {
        Task t = taskService.findById(idTask);
        model.addAttribute("taskDt", t);
        return "taskHome/edit-task";
    }

    /**
     * Procesa el formulario de edicion de una tarea.
     * - Si hay errores de validacion o el tiempo (si se requiere) no está establecido, retorna al formulario con los errores.
     * - Si no la guarda en la base de datos
     */
    @PostMapping("/editTask/submit")
    public String updateTask(@RequestParam("id") Long idTask, @Valid @ModelAttribute("taskDt") Task editTask, BindingResult result) {
        if (result.hasErrors()) {
            return "taskHome/edit-task";
        } else {
            if (taskService.timeNullValid(editTask)) {
                result.rejectValue("time", "time.error");
                return "taskHome/edit-task";
            }
        }
        Task taskOriginal = taskService.findById(idTask);

        // Copiar los campos editables desde editTask a originalTask, excluyendo el ID y el usuario
        BeanUtils.copyProperties(editTask, taskOriginal, "id", "myUser");
        taskService.save(taskOriginal);
        return "redirect:/home";
    }

    /**
     * Actualiza el estado de la tarea
     */
    @PostMapping("/updateStatus/{id}")
    public String updateTaskStatus(@PathVariable("id") Long taskId, @RequestParam("status") Status newStatus) {
        taskService.updateStatus(taskId, newStatus);
        return "redirect:/home";
    }

    /**
     * Obtiene el orden seleccionado por el usuario y crea una cookie con ese valor
     */
    @GetMapping("/sortBy")
    public String sortBy(@RequestParam("sortOption") String sortOption, HttpServletResponse response) {
        if (sortOption == null) {
            sortOption = "status";
        }


        Cookie cookieSort = new Cookie("sortOption", sortOption);
        cookieSort.setMaxAge(COOKIE_MAX_RANGE);
        cookieSort.setPath("/home");

        response.addCookie(cookieSort);

        return "redirect:/home";
    }

    /**
     * Guarda los filtros de las tareas en la sesión
     */
    @PostMapping("/searchFilter/submit")
    public String searchFilter(@ModelAttribute("searchFilter") FiltrosTask filtrosTask, HttpSession session) {
        session.setAttribute("filtrosTask", filtrosTask);
        return "redirect:/home";
    }

    /**
     * Elimina los filtros de la sesión
     */
     @GetMapping("/deleteFilters")
    public String deleteFilters(HttpSession session) {
        session.removeAttribute("filtrosTask");
        return "redirect:/home";
    }

    /**
     * Retorna el usuario actual
     */
    private MyUser usuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            return userService.findByUsername(username);
        }
        return null;
    }

    /**
     * Ordena la lista de tareas y la agrega a un modelo.
     *
     */
    private void sortListAndAddToModel(String sortOption, List<Task> taskList, Model model) {
        switch (sortOption) {
            case "title" -> model.addAttribute("taskList", taskService.sortByTitle(taskList));
            case "date" -> model.addAttribute("taskList", taskService.sortByDate(taskList));
            case "status" -> model.addAttribute("taskList", taskService.sortByStatus(taskList));
        }
    }
}
