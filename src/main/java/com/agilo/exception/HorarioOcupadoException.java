package com.agilo.exception;

import java.time.LocalDate;
import java.time.LocalTime;

public class HorarioOcupadoException extends BusinessException {
    public HorarioOcupadoException(LocalDate dataMarcada,LocalTime horaInicio, LocalTime horaFim) {
        super("Horário já ocupado: "+
                horaInicio+" - "+
                horaFim +" | "+
                dataMarcada);
    }
}
