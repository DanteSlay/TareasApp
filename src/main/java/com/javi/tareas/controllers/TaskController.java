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

@Slf4j
@Controller
public class TaskController {
    @Autowired
    private TaskServices taskService;

    private Long userId;

    private void sortAndAddToModel(String sortOption, Model model) {
        switch (sortOption) {
            case "title" -> model.addAttribute("taskList", taskService.sortByTitle(userId));
            case "date" -> model.addAttribute("taskList", taskService.sortByDate(userId));
            case "status" -> model.addAttribute("taskList", taskService.sortByStatus(userId));
        }
    }

    @GetMapping("/home/{id}")
    public String home(@PathVariable("id") Long id, Model model, HttpSession session) {
        userId = id;
        String sortOption = (String) session.getAttribute("sortOption");
        if (sortOption == null) {
            model.addAttribute("taskList", taskService.sortByStatus(userId));
            return "index";
        }

        sortAndAddToModel(sortOption, model);
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
        return "new-task";
    }

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
        if (newTask.getStatus() == null) newTask.setStatus(Status.PENDING);
        if (newTask.getAllDay()) newTask.setTime(null);

        taskService.add(newTask);
        return "redirect:/home/" + newTask.getIdUser();
    }

    @GetMapping("/viewTask/{id}")
    public String viewTask(@PathVariable("id") Long id, Model model) {
        model.addAttribute("taskDt", taskService.find(id));
        return "task";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idTask) {
        taskService.delete(idTask);
        return "redirect:/home/" + userId;
    }

    @GetMapping("/viewTask/edit/{id}")
    public String edit(@PathVariable("id") Long idTask, Model model) {
        Task t = taskService.find(idTask);
        model.addAttribute("taskDt", t);
        return "edit-task";
    }

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

    @PostMapping("/home/updateStatus/{id}")
    public String updateTaskStatus(@PathVariable("id") Long taskId, @RequestParam("status") Status newStatus) {
        taskService.updateStatus(taskId, newStatus);
        return "redirect:/home/" + userId;
    }

    @PostMapping("/home/sortBy/submit")
    public String sortBy(@RequestParam("sortOption") String sortOption, Model model, HttpSession session) {
        session.setAttribute("sortOption", sortOption);

        sortAndAddToModel(sortOption, model);

        return "redirect:/home/" + userId;
    }

}
