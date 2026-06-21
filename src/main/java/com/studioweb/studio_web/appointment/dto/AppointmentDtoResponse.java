package com.studioweb.studio_web.appointment.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AppointmentDtoResponse {

    public Long id;
    public LocalDate dataMarcada;
    public LocalTime horaInicio;
    public Long clientId;
    public Long userId;
    public LocalTime horaFim;
    public String descricao;
    public String anotacao;
    public Integer AppointmentStatus;
    public LocalDateTime criadoEm;
}
