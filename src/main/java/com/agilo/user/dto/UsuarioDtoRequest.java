package com.agilo.user.dto;

import com.agilo.user.UsuarioRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record UsuarioDtoRequest(
        @NotBlank String nome,
        @NotBlank String usuario,
        @NotBlank String senha,
        @NotNull UsuarioRole role
){
}
