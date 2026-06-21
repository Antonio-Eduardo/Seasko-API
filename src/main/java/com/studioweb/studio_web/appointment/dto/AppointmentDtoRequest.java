package com.studioweb.studio_web.appointment.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentDtoRequest {

    public LocalDate dataMarcada;
    public LocalTime horaInicio;
    public Long clientId;
    public Long userId;
    public LocalTime horaFim;
    public String descricao;
    public String anotacao;
    public Integer AppointmentStatus;
}
