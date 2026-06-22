package com.studioweb.studio_web.appointment.dto;

import com.studioweb.studio_web.appointment.AgendamentoStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoDtoResponse(
        Long id,
        LocalDate dataMarcada,
        LocalTime horaInicio,
        LocalTime horaFim,
        AgendamentoStatus status,
        String descricao,
        Long clientId,
        Long userId
) {}
