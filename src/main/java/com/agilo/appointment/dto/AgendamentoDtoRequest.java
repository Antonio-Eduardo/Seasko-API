package com.agilo.appointment.dto;

import com.agilo.appointment.AgendamentoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoDtoRequest(
        @NotNull LocalDate dataMarcada,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFim,
        @NotNull Long clientId,
        @NotNull Long userId,
        @NotNull AgendamentoStatus status,
        @NotBlank String descricao,
        @NotBlank String anotacao
) {}