package com.javi.tareas.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        //indicamos las rutas que se pueden mostrar sin hacer login
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/login/**"),
                                AntPathRequestMatcher.antMatcher("/changeLanguage"),
                                AntPathRequestMatcher.antMatcher("/webjars/**"),
                                AntPathRequestMatcher.antMatcher("/css/**"),
                                PathRequest.toH2Console()).permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        // Indicamos que despues de hacer login, siempre nos llevará a una URL, asi los ?error/?continue no serán un problema
                        .defaultSuccessUrl("/home", true)
                        .permitAll())
                .logout(out -> out
                        .logoutUrl("/login/logout").permitAll());

        // Para que funcione la consola del h2
        http.csrf(csrf ->
                csrf.ignoringRequestMatchers(
                        AntPathRequestMatcher.antMatcher("/"),
                        AntPathRequestMatcher.antMatcher("/webjars/**"),
                        AntPathRequestMatcher.antMatcher("/css/**"),
                        PathRequest.toH2Console()));

        // Para que funcione la consola del h2
        http.headers(headers ->
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();

    }

}
