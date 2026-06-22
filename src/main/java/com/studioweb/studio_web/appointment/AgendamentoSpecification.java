package com.studioweb.studio_web.appointment;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AgendamentoSpecification {

    public static Specification<Agendamento> porData(LocalDate data) {
        return (root, query, cb) ->
                data == null ? null : cb.equal(root.get("dataMarcada"), data);
    }

    public static Specification<Agendamento> porMes(Integer mes, Integer ano) {
        return (root, query, cb) -> {
            if (mes == null) return null;
            int anoFinal = ano != null ? ano : LocalDate.now().getYear();
            LocalDate inicio = LocalDate.of(anoFinal, mes, 1);
            LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
            return cb.between(root.get("dataMarcada"), inicio, fim);
        };
    }

    public static Specification<Agendamento> porCliente(Long clienteId) {
        return (root, query, cb) ->
                clienteId == null ? null : cb.equal(root.get("client").get("id"), clienteId);
    }

    public static Specification<Agendamento> porUsuario(Long usuarioId) {
        return (root, query, cb) ->
                usuarioId == null ? null : cb.equal(root.get("user").get("id"), usuarioId);
    }

    public static Specification<Agendamento> porStatus(AgendamentoStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }
}
