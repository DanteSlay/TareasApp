package com.javi.tareas.repositories;

import com.javi.tareas.entities.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> {
    MyUser findByEmail(String email);
    MyUser findByEmailAndPassword(String email, String password);
    MyUser findByUsername(String username);
}
