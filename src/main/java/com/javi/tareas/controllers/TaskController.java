package com.javi.tareas.controllers;

import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import com.javi.tareas.services.TaskServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Slf4j
@Controller
public class TaskController {
    @Autowired
    private TaskServices service;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("taskList", service.findAll());
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

    @GetMapping("/delete/{title}")
    public String delete(@PathVariable("title") String title) {
        service.delete(title);
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
            service.add(task);
            return "redirect:/home";
        }
    }
}