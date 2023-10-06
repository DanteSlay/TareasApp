package com.javi.tareas.controllers;

import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import com.javi.tareas.services.TaskServices;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Controller
public class TaskController {
    @Autowired
    private TaskServices taskService;

    private Long userId;

    @GetMapping( "/home/{id}")
    public String home(@PathVariable("id") Long id, Model model) {
        userId = id;
        model.addAttribute("taskList", taskService.findAll(userId));
        return "index";
    }

    @GetMapping("/newTask")
    public String newTask(Model model) {
        Task task = Task.builder()
                .dueDate(LocalDate.now())
                .time(LocalTime.now())
                .idUser(userId)
                .build();
        model.addAttribute("taskDt", task);
        return "form";
    }

    @GetMapping("/viewTask/{id}")
    public String viewTask(@PathVariable("id") Long id, Model model) {
        model.addAttribute("taskDt", taskService.find(id));
        return "task";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idTask) {
        taskService.delete(idTask);
        return "redirect:/home" + userId;
    }

    @GetMapping("/viewTask/edit/{id}")
    public String edit(@PathVariable("id") Long idTask, Model model) {
        Task t = taskService.find(idTask);
        model.addAttribute("taskDt", t);
        log.info(String.valueOf(t));
        return "edit-task";
    }

    @PostMapping("/newTask/submit")
    public String newTask(@Valid @ModelAttribute("taskDt") Task task,
                          BindingResult result) {
        if (result.hasErrors()) {
            return "form";
        } else {
            if (task.getStatus() == null) task.setStatus(Status.PENDING);
            if (task.getAllDay()) task.setTime(null);
            task.setIdUser(userId);

            taskService.add(task);
            return "redirect:/home/" + userId;
        }
    }
        @PostMapping("home/updateStatus/{id}")
        public String updateTaskStatus(@PathVariable("id") Long taskId, @RequestParam("status") Status newStatus) {
            taskService.updateStatus(taskId, newStatus);
            return "redirect:/home/" + userId;
        }

    @PostMapping("/editTask/submit")
    public String updateTask(@Valid @ModelAttribute("taskDt") Task editTask, BindingResult result) {
        log.info(String.valueOf(editTask));
        if (result.hasErrors()) return "edit-task";

        taskService.updateTask(editTask);
        return "redirect:/viewTask/" + editTask.getId();
    }
}
