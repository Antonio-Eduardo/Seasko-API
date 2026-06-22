package com.vettore.user.dto;

import com.vettore.user.UsuarioRole;

public record UsuarioDtoResponse(
        Long id,
        String usuario,
        UsuarioRole role,
        Boolean ativo
) {
}
