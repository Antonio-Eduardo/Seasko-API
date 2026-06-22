package com.vettore.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long>, JpaSpecificationExecutor<Agendamento> {
}
