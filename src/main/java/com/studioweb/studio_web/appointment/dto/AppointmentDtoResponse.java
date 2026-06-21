package com.studioweb.studio_web.appointment.dto;

import com.studioweb.studio_web.appointment.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentDtoResponse(
        Long id,
        LocalDate dataMarcada,
        LocalTime horaInicio,
        LocalTime horaFim,
        AppointmentStatus status,
        String descricao,
        Long clientId,
        Long userId
) {}
