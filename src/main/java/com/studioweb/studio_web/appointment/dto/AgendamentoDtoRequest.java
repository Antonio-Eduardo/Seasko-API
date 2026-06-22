package com.studioweb.studio_web.appointment.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoDtoRequest(
        LocalDate dataMarcada,
        LocalTime horaInicio,
        LocalTime horaFim,
        Long userId,
        String descricao,
        String anotacao
) {}