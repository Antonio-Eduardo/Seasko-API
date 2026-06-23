package com.agilo.client.dto;

import jakarta.validation.constraints.NotBlank;

public record ClienteDtoRequest(
        @NotBlank String nome,
        @NotBlank String telefone,
        String anotacao) {
}
