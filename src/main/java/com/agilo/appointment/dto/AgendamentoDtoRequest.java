package com.agilo.appointment.dto;

import com.agilo.appointment.AgendamentoStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoDtoRequest(
        LocalDate dataMarcada,
        LocalTime horaInicio,
        LocalTime horaFim,
        Long clientId,
        Long userId,
        AgendamentoStatus status,
        String descricao,
        String anotacao
) {}