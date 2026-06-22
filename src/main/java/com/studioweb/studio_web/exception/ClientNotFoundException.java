package com.studioweb.studio_web.exception;

public class ClientNotFoundException extends NotFoundException {
    public ClientNotFoundException(Long id) {
        super("Cliente não encontrado: " + id);
    }
}
