package com.agilo.appointment.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoDtoRequest(
        LocalDate dataMarcada,
        LocalTime horaInicio,
        LocalTime horaFim,
        Long clientId,
        Long userId,
        String descricao,
        String anotacao
) {}