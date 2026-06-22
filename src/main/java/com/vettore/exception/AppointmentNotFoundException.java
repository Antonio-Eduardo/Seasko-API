package com.vettore.exception;

public class AppointmentNotFoundException extends NotFoundException {
    public AppointmentNotFoundException(Long id) {
        super("Agendamento não encontrado: " + id);
    }
}
