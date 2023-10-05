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
    private static long lastId = 1;

    private long id;

    @NotBlank(message = "{user.nameBlank.error}")
    private String username;
    @Email(message = "{user.email.error}")
    private String email;

    @Size(min = 8, max = 20)
    private String password;

/*    public User() {
        this.id = ++lastId;
    }*/

    public User generateId(User user) {
        user.setId(++lastId);
        return user;
    }
}
