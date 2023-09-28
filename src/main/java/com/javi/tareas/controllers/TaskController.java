package com.javi.tareas.controllers;

import com.javi.tareas.entities.Status;
import com.javi.tareas.entities.Task;
import com.javi.tareas.services.TaskServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
        model.addAttribute("taskDt", new Task());
        return "form";
    }

    @GetMapping("/delete/{title}")
    public String delete(@PathVariable("title") String title, Model model) {
        service.delete(title);
        return "index";
    }

    @PostMapping("/new")
    public String newTask(@Valid @ModelAttribute("taskDt") Task task,
                          BindingResult result) {
        if (result.hasErrors()) {
            return "form";
        } else {
            if (task.getStatus() == null) task.setStatus(Status.PENDING);
            service.add(task);
            return "redirect:/home";
        }
    }
}