package com.studioweb.studio_web.appointment;

import com.studioweb.studio_web.client.Client;
import com.studioweb.studio_web.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.mapping.Join;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String descricao;
    private LocalDate dataMarcada;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private LocalDateTime criadoEm;
    private String anotacao;
}
