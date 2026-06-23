package com.agilo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/public/**").permitAll()
                        .requestMatchers("/favicon.svg").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuario/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/usuario/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/usuario/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/agendamento/deletar/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/cliente/deletar/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/usuario/**").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
