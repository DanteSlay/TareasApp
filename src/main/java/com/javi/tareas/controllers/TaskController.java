package com.javi.tareas.controllers;

import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import com.javi.tareas.services.TaskServices;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Esta clase maneja las solicitudes relacionadas con la creación, visualización, edición, eliminación y ordenación de tareas.
 * Además, gestiona la selección de opciones de ordenación y el cambio de estado de las tareas.
 */
@Slf4j // Anotación que habilita la funcionalidad de registro (logging) en la clase.
@Controller // Marca esta clase como un controlador de Spring MVC.
public class TaskController {
    @Autowired // Inyecta una instancia del bean TaskServices
    private TaskServices taskService;

    /**
     * Variable que guarda el ID del usuario que entra en este controlador
     */
    private Long userId;

    /**
     * Método privado que se encarga de ordenar una lista de tareas y agregarla a un modelo, según la opción de ordenación especificada.
     *
     * @param sortOption La opción de ordenación que determina cómo se ordenarán las tareas.
     * @param model El modelo al que se añadirá la lista de tareas ordenadas.
     */
    private void sortAndAddToModel(String sortOption, Model model) {
        switch (sortOption) {
            case "title" -> model.addAttribute("taskList", taskService.sortByTitle(userId));
            case "date" -> model.addAttribute("taskList", taskService.sortByDate(userId));
            case "status" -> model.addAttribute("taskList", taskService.sortByStatus(userId));
        }
    }

    /**
     * Método controlador que se encarga de mostrar la página principal de tareas para un usuario específico.
     *
     * @param id     El ID del usuario para el cual se mostrarán las tareas.
     * @param model  El modelo utilizado para pasar datos a la vista.
     * @param session La sesión HTTP utilizada para almacenar la opción de ordenación actual.
     * @return La vista "index" que muestra las tareas del usuario.
     */
    @GetMapping("/home/{id}")
    public String home(@PathVariable("id") Long id, Model model, HttpSession session) {
        // Establece el ID del usuario actual
        userId = id;

        // Obtenemos la opción de ordenación actual almacenada en la sesión, si aún no hay ninguna almacenada se utilizará "status" por defecto
        String sortOption = (String) session.getAttribute("sortOption");
        if (sortOption == null) {
            sortOption = "status";
        }

        // Ordenamos las tareas y las añadimos al modelo
        sortAndAddToModel(sortOption, model);
        return "index";
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
    public String newTask(Model model) {
        Task task = Task.builder()
                .dueDate(LocalDate.now())
                .time(LocalTime.now())
                .idUser(userId)
                .build();
        model.addAttribute("taskDt", task);
        return "new-task";
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

        taskService.add(newTask);
        return "redirect:/home/" + newTask.getIdUser();
    }

    /**
     * Método controlador que muestra los detalles de una tarea específica.
     *
     * @param id    El ID de la tarea que se desea ver.
     * @param model El modelo utilizado para pasar datos a la vista.
     * @return La vista "task" que muestra los detalles de la tarea.
     */
    @GetMapping("/viewTask")
    public String viewTask(@RequestParam("id") Long id, Model model) {
        model.addAttribute("taskDt", taskService.find(id));
        return "task";
    }

    /**
     * Método controlador que elimina una tarea específica del repositorio de tareas.
     *
     * @param idTask El ID de la tarea que se desea eliminar.
     * @return Redirige al usuario a su página de inicio después de eliminar la tarea.
     */
    @GetMapping("/deleteTask")
    public String delete(@RequestParam("task") Long idTask) {
        taskService.delete(idTask);
        return "redirect:/home/" + userId;
    }

    /**
     * Método controlador que permite la edición de una tarea específica.
     *
     * @param idTask El ID de la tarea que se desea editar.
     * @param model  El modelo utilizado para pasar datos a la vista de edición.
     * @return La vista "edit-task", un formulario con los detalles de la tarea que se desea editar.
     */
    @GetMapping("/editTask")
    public String edit(@RequestParam("idTask") Long idTask, Model model) {
        Task t = taskService.find(idTask);
        model.addAttribute("taskDt", t);
        return "edit-task";
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
     * @param result    El resultado de la validación que se utilizará para detectar errores.
     * @return Si hay errores de validación, retorna la vista "edit-task". Si la tarea se actualiza exitosamente,
     * redirige al usuario a la vista de detalles de la tarea.
     */
    @PostMapping("/editTask/submit")
    public String updateTask(@Valid @ModelAttribute("taskDt") Task editTask, BindingResult result) {
        if (result.hasErrors()) {
            return "edit-task";
        } else {
            if (taskService.timeNullValid(editTask)) {
                result.rejectValue("time", "time.error");
                return "edit-task";
            }
        }

        taskService.updateTask(editTask);
        return "redirect:/viewTask/" + editTask.getId();
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
     * @param model      El modelo utilizado para pasar datos a la vista.
     * @param session    La sesión del usuario donde se guarda la opción de ordenación.
     * @return Redirige al usuario a su página de inicio con las tareas ordenadas.
     */
    @PostMapping("/home/sortBy/submit")
    public String sortBy(@RequestParam("sortOption") String sortOption, Model model, HttpSession session) {
        if (sortOption == null) {
            sortOption = "status";
        }
        session.setAttribute("sortOption", sortOption);

        sortAndAddToModel(sortOption, model);

        return "redirect:/home/" + userId;
    }

}
