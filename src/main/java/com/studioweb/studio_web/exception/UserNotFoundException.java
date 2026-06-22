package com.studioweb.studio_web.exception;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(Long id) {
        super("Usuário não encontrado: " + id);
    }
}
