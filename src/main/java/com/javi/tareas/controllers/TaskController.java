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
    private TaskServices TaskService;

    private Long userId;

    @GetMapping( "/home/{id}")
    public String home(@PathVariable("id") Long id, Model model) {
        userId = id;
        model.addAttribute("taskList", TaskService.findAll(userId));
        return "index";
    }

    @GetMapping("/new")
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
        model.addAttribute("taskDt", TaskService.find(id));
        return "task";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idTask) {
        TaskService.delete(idTask);
        return "redirect:/home" + userId;
    }

    @PostMapping("/new/submit")
    public String newTask(@Valid @ModelAttribute("taskDt") Task task,
                          BindingResult result) {
        if (result.hasErrors()) {
            return "form";
        } else {
            if (task.getStatus() == null) task.setStatus(Status.PENDING);
            if (task.getAllDay()) task.setTime(null);
            task.setIdUser(userId);

            TaskService.add(task);
            return "redirect:/home/" + userId;
        }
    }

}