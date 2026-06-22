package com.vettore.client.dto;

import java.time.LocalDateTime;


public record ClienteDtoResponse(
        Long id,
        String nome,
        String telefone,
        String anotacao,
        LocalDateTime criadoEm
) {}
