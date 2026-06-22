package com.agilo.user.dto;

import com.agilo.user.UsuarioRole;

public record UsuarioDtoResponse(
        Long id,
        String usuario,
        UsuarioRole role,
        Boolean ativo
) {
}
