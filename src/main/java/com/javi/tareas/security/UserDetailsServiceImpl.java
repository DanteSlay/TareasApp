package com.javi.tareas.security;


import com.javi.tareas.entities.MyUser;
import com.javi.tareas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MyUser myUser = userRepository.findByEmail(email);
        return User.builder()
                .username(myUser.getUsername())
                .password(myUser.getPassword())
                .authorities(new SimpleGrantedAuthority(myUser.getRole()))
                .disabled(false)
                .build();
    }
}
