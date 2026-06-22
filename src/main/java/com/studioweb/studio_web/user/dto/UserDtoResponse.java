package com.studioweb.studio_web.user.dto;

import com.studioweb.studio_web.user.UserRole;

public record UserDtoResponse(
        Long id,
        String usuario,
        UserRole role,
        Boolean ativo
) {
}
