package com.studioweb.studio_web.user.dto;

import com.studioweb.studio_web.user.UsuarioRole;

public record UserDtoRequest(
        String nome,
        String usuario,
        String senha,
        UsuarioRole role
){
}
