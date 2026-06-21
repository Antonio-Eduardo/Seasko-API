package com.studioweb.studio_web.client.dto;

import java.time.LocalDateTime;


public record ClientDtoResponse(
        Long id,
        String nome,
        String telefone,
        String anotacao,
        LocalDateTime criadoEm
) {}
