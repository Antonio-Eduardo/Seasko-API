package com.studioweb.studio_web.client.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientDtoResponse {

    public Long id;
    public String nome;
    public String telefone;
    public String anotacao;
    public LocalDateTime criadoEm;
}
