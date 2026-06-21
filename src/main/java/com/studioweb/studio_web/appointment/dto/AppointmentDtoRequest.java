package com.studioweb.studio_web.appointment.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentDtoRequest(
        LocalDate dataMarcada,
        LocalTime horaInicio,
        Long clientId,
        Long userId,
        String descricao
) {}