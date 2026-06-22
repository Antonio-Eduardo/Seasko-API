package com.studioweb.studio_web.user.dto;

import com.studioweb.studio_web.user.UsuarioRole;

public record UserDtoResponse(
        Long id,
        String usuario,
        UsuarioRole role,
        Boolean ativo
) {
}
