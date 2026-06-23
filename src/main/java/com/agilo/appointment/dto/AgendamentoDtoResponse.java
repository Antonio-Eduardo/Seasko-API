package com.agilo.appointment.dto;

import com.agilo.appointment.AgendamentoStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoDtoResponse(
        Long id,
        LocalDate dataMarcada,
        LocalTime horaInicio,
        LocalTime horaFim,
        AgendamentoStatus status,
        String descricao,
        String anotacao,
        Long clientId,
        String clientNome,
        Long userId,
        String userNome
) {}
