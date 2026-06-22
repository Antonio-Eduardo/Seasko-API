package com.agilo.appointment;

import com.agilo.appointment.dto.AgendamentoDtoRequest;
import com.agilo.appointment.dto.AgendamentoDtoResponse;
import com.agilo.client.Cliente;
import com.agilo.client.ClienteRepository;
import com.agilo.exception.AppointmentNotFoundException;
import com.agilo.exception.ClientNotFoundException;
import com.agilo.exception.UserNotFoundException;
import com.agilo.user.Usuario;
import com.agilo.user.UsuarioRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository appointmentRepository;
    private final ClienteRepository clientRepository;
    private final UsuarioRepository userRepository;

    public AgendamentoService(AgendamentoRepository appointmentRepository,
                              ClienteRepository clientRepository,
                              UsuarioRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    public List<AgendamentoDtoResponse> getAllAppointments(
            LocalDate data,
            Integer mes,
            Integer ano,
            Long clienteId,
            Long usuarioId,
            AgendamentoStatus status) {

        var spec = Specification
                .where(AgendamentoSpecification.porData(data))
                .and(AgendamentoSpecification.porMes(mes, ano))
                .and(AgendamentoSpecification.porCliente(clienteId))
                .and(AgendamentoSpecification.porUsuario(usuarioId))
                .and(AgendamentoSpecification.porStatus(status));

        return appointmentRepository.findAll(spec).stream()
                .map(this::toResponse)
                .toList();
    }

    public AgendamentoDtoResponse getAppointmentById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public AgendamentoDtoResponse insertAppointment(AgendamentoDtoRequest dto) {

        Cliente client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new ClientNotFoundException(dto.clientId()));
        Usuario user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(dto.userId()));

        Agendamento appointment = new Agendamento();
        appointment.setClient(client);
        appointment.setUser(user);
        appointment.setDescricao(dto.descricao());
        appointment.setAnotacao(dto.anotacao());
        appointment.setDataMarcada(dto.dataMarcada());
        appointment.setHoraInicio(dto.horaInicio());
        appointment.setHoraFim(dto.horaFim());
        appointment.setStatus(AgendamentoStatus.MARCADO);
        appointment.setCriadoEm(LocalDateTime.now());

        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AgendamentoDtoResponse updateAppointment(Long id, AgendamentoDtoRequest dto) {
        Agendamento appointment = findOrThrow(id);

        Usuario user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(dto.userId()));

        appointment.setUser(user);
        appointment.setAnotacao(dto.anotacao());
        appointment.setDescricao(dto.descricao());
        appointment.setDataMarcada(dto.dataMarcada());
        appointment.setHoraInicio(dto.horaInicio());
        appointment.setHoraFim(dto.horaFim());

        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AgendamentoDtoResponse updateStatus(Long id, AgendamentoStatus status) {
        Agendamento appointment = findOrThrow(id);
        appointment.setStatus(status);
        return toResponse(appointmentRepository.save(appointment));
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new AppointmentNotFoundException(id);
        }
        appointmentRepository.deleteById(id);
    }

    private Agendamento findOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    private AgendamentoDtoResponse toResponse(Agendamento appointment) {
        return new AgendamentoDtoResponse(
                appointment.getId(),
                appointment.getDataMarcada(),
                appointment.getHoraInicio(),
                appointment.getHoraFim(),
                appointment.getStatus(),
                appointment.getDescricao(),
                appointment.getAnotacao(),
                appointment.getClient().getId(),
                appointment.getUser().getId()
        );
    }
}
