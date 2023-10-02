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

    @GetMapping( "/home")
    public String home(Model model) {
        model.addAttribute("taskList", TaskService.findAll());
        return "index";
    }

    @GetMapping("/new")
    public String newTask(Model model) {
        Task task = Task.builder()
                .dueDate(LocalDate.now())
                .time(LocalTime.now())
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
    public String delete(@PathVariable("id") Long id) {
        TaskService.delete(id);
        return "redirect:/home";
    }

    @PostMapping("/new")
    public String newTask(@Valid @ModelAttribute("taskDt") Task task,
                          BindingResult result) {
        if (result.hasErrors()) {
            return "form";
        } else {
            if (task.getStatus() == null) task.setStatus(Status.PENDING);
            if (task.getAllDay()) task.setTime(null);
            TaskService.add(task);
            return "redirect:/home";
        }
    }
}