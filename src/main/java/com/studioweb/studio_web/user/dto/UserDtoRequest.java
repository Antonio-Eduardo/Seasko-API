package com.studioweb.studio_web.user.dto;

import com.studioweb.studio_web.user.UserRole;

public record UserDtoRequest(
        String nome,
        String usuario,
        String senha,
        UserRole role
){
}
