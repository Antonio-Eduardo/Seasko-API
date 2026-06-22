package com.vettore.exception;

public class ClientNotFoundException extends NotFoundException {
    public ClientNotFoundException(Long id) {
        super("Cliente não encontrado: " + id);
    }
}
