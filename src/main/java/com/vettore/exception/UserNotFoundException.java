package com.vettore.exception;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(Long id) {
        super("Usuário não encontrado: " + id);
    }
}
