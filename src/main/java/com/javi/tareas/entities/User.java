package com.javi.tareas.entities;

import com.javi.tareas.services.TaskServices;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @NotBlank(message = "{user.nameBlank.error}")
    private String username;
    @Email(message = "{user.email.error}")
    private String email;

    @Size(min = 8, max = 20)
    private String password;

    private TaskServices taskList;
}
