package com.vettore.appointment.dto;

import com.vettore.appointment.AgendamentoStatus;
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
        Long userId
) {}
