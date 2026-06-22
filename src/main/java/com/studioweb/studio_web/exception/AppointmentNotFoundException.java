package com.studioweb.studio_web.exception;

public class AppointmentNotFoundException extends NotFoundException {
    public AppointmentNotFoundException(Long id) {
        super("Agendamento não encontrado: " + id);
    }
}
