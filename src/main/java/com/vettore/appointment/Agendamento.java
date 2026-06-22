package com.vettore.appointment;

import com.vettore.client.Cliente;
import com.vettore.user.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_agendamento")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente client;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario user;
    private String descricao;
    private LocalDate dataMarcada;
    @Enumerated(EnumType.STRING)
    private AgendamentoStatus status;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private LocalDateTime criadoEm;
    private String anotacao;
}
