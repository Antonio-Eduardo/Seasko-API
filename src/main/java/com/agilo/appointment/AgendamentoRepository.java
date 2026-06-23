package com.agilo.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long>, JpaSpecificationExecutor<Agendamento> {

    @Query("""
SELECT COUNT(a) > 0
FROM Agendamento a
WHERE a.dataMarcada = :data
AND a.horaInicio < :horaFim
AND a.horaFim > :horaInicio
""")
    boolean existsConflitoHorario(
            LocalDate data,
            LocalTime horaInicio,
            LocalTime horaFim
    );
    @Query("""
SELECT COUNT(a) > 0 FROM Agendamento a
WHERE a.dataMarcada = :data
AND a.horaInicio < :horaFim
AND a.horaFim > :horaInicio
AND a.id <> :id
""")
    boolean existsConflitoHorarioExcluindo(LocalDate data,
                                           LocalTime horaInicio,
                                           LocalTime horaFim,
                                           Long id);
}
