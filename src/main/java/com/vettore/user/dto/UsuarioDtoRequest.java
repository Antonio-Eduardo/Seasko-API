package com.vettore.user.dto;

import com.vettore.user.UsuarioRole;

public record UsuarioDtoRequest(
        String nome,
        String usuario,
        String senha,
        UsuarioRole role
){
}
