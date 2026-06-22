package com.studioweb.studio_web.user.dto;

import com.studioweb.studio_web.user.UsuarioRole;

public record UsuarioDtoRequest(
        String nome,
        String usuario,
        String senha,
        UsuarioRole role
){
}
