package com.agilo.user.dto;

import com.agilo.user.UsuarioRole;

public record UsuarioDtoRequest(
        String nome,
        String usuario,
        String senha,
        UsuarioRole role
){
}
