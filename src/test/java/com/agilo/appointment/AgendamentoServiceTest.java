package com.agilo.appointment;

import com.agilo.appointment.dto.AgendamentoDtoRequest;
import com.agilo.appointment.dto.AgendamentoDtoResponse;
import com.agilo.client.Cliente;
import com.agilo.client.ClienteRepository;
import com.agilo.common.PageResponse;
import com.agilo.exception.AppointmentNotFoundException;
import com.agilo.exception.ClientNotFoundException;
import com.agilo.exception.UserNotFoundException;
import com.agilo.user.Usuario;
import com.agilo.user.UsuarioRepository;
import com.agilo.user.UsuarioRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository appointmentRepository;

    @Mock
    private ClienteRepository clientRepository;

    @Mock
    private UsuarioRepository userRepository;

    @InjectMocks
    private AgendamentoService service;

    private Cliente cliente;
    private Usuario usuario;
    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setTelefone("11999990000");
        cliente.setAnotacao("");
        cliente.setCriadoEm(LocalDateTime.now());

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Admin");
        usuario.setUsuario("admin");
        usuario.setSenha("hash");
        usuario.setRole(UsuarioRole.ADMIN);
        usuario.setAtivo(true);

        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setClient(cliente);
        agendamento.setUser(usuario);
        agendamento.setDescricao("Corte");
        agendamento.setDataMarcada(LocalDate.of(2026, 7, 1));
        agendamento.setHoraInicio(LocalTime.of(9, 0));
        agendamento.setHoraFim(LocalTime.of(10, 0));
        agendamento.setStatus(AgendamentoStatus.MARCADO);
        agendamento.setCriadoEm(LocalDateTime.now());
        agendamento.setAnotacao("");
    }

    @Test
    void getAllAppointments_returnsMappedList() {
        Pageable pageable = PageRequest.of(0, 25);
        when(appointmentRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(agendamento), pageable, 1));

        PageResponse<AgendamentoDtoResponse> result = service.getAllAppointments(
                null, null, null, null, null, null, pageable
        );

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.content().get(0).id()).isEqualTo(1L);
        assertThat(result.content().get(0).status()).isEqualTo(AgendamentoStatus.MARCADO);
        assertThat(result.content().get(0).clientNome()).isEqualTo("João");
        assertThat(result.content().get(0).userNome()).isEqualTo("Admin");
    }

    @Test
    void getAllAppointments_emptyRepository_returnsEmptyList() {
        Pageable pageable = PageRequest.of(0, 25);
        when(appointmentRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        PageResponse<AgendamentoDtoResponse> result = service.getAllAppointments(null, null, null, null, null, null, pageable);
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    @Test
    void getAppointmentById_found_returnsResponse() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        AgendamentoDtoResponse result = service.getAppointmentById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.descricao()).isEqualTo("Corte");
        assertThat(result.dataMarcada()).isEqualTo(LocalDate.of(2026, 7, 1));
    }

    @Test
    void getAppointmentById_notFound_throwsAppointmentNotFoundException() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAppointmentById(99L))
                .isInstanceOf(AppointmentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void insertAppointment_validIds_setsStatusMarcadoAndCriadoEm() {
        AgendamentoDtoRequest dto = new AgendamentoDtoRequest(
                LocalDate.of(2026, 7, 1),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                1L, 1L, null, "Corte", ""
        );
        when(appointmentRepository.existsConflitoHorario(any(), any(), any())).thenReturn(false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(appointmentRepository.save(any(Agendamento.class))).thenReturn(agendamento);

        AgendamentoDtoResponse result = service.insertAppointment(dto);

        assertThat(result.status()).isEqualTo(AgendamentoStatus.MARCADO);

        ArgumentCaptor<Agendamento> captor = ArgumentCaptor.forClass(Agendamento.class);
        verify(appointmentRepository).save(captor.capture());
        Agendamento saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(AgendamentoStatus.MARCADO);
        assertThat(saved.getCriadoEm()).isNotNull();
        assertThat(saved.getClient()).isEqualTo(cliente);
        assertThat(saved.getUser()).isEqualTo(usuario);
    }

    @Test
    void insertAppointment_clientNotFound_throwsClientNotFoundException() {
        AgendamentoDtoRequest dto = new AgendamentoDtoRequest(
                LocalDate.of(2026, 7, 1),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                99L, 1L, null, "Corte", ""
        );
        when(appointmentRepository.existsConflitoHorario(any(), any(), any())).thenReturn(false);
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.insertAppointment(dto))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining("99");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void insertAppointment_userNotFound_throwsUserNotFoundException() {
        AgendamentoDtoRequest dto = new AgendamentoDtoRequest(
                LocalDate.of(2026, 7, 1),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                1L, 99L, null, "Corte", ""
        );
        when(appointmentRepository.existsConflitoHorario(any(), any(), any())).thenReturn(false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.insertAppointment(dto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void updateAppointment_found_updatesFields() {
        AgendamentoDtoRequest dto = new AgendamentoDtoRequest(
                LocalDate.of(2026, 8, 1),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                1L, 1L, AgendamentoStatus.CONFIRMADO, "Coloração", "observação"
        );
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(appointmentRepository.existsConflitoHorarioExcluindo(any(), any(), any(), any())).thenReturn(false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(appointmentRepository.save(any(Agendamento.class))).thenReturn(agendamento);

        service.updateAppointment(1L, dto);

        ArgumentCaptor<Agendamento> captor = ArgumentCaptor.forClass(Agendamento.class);
        verify(appointmentRepository).save(captor.capture());
        Agendamento saved = captor.getValue();
        assertThat(saved.getDescricao()).isEqualTo("Coloração");
        assertThat(saved.getAnotacao()).isEqualTo("observação");
        assertThat(saved.getDataMarcada()).isEqualTo(LocalDate.of(2026, 8, 1));
        assertThat(saved.getHoraInicio()).isEqualTo(LocalTime.of(14, 0));
        assertThat(saved.getHoraFim()).isEqualTo(LocalTime.of(15, 0));
    }

    @Test
    void updateAppointment_notFound_throwsAppointmentNotFoundException() {
        AgendamentoDtoRequest dto = new AgendamentoDtoRequest(
                LocalDate.of(2026, 7, 1),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                1L, 1L, null, "Corte", ""
        );
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateAppointment(99L, dto))
                .isInstanceOf(AppointmentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateStatus_found_updatesStatus() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(appointmentRepository.save(any(Agendamento.class))).thenReturn(agendamento);

        service.updateStatus(1L, AgendamentoStatus.CONFIRMADO);

        ArgumentCaptor<Agendamento> captor = ArgumentCaptor.forClass(Agendamento.class);
        verify(appointmentRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(AgendamentoStatus.CONFIRMADO);
    }

    @Test
    void updateStatus_notFound_throwsAppointmentNotFoundException() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(99L, AgendamentoStatus.CANCELADO))
                .isInstanceOf(AppointmentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteAppointment_exists_deletesSuccessfully() {
        when(appointmentRepository.existsById(1L)).thenReturn(true);

        service.deleteAppointment(1L);

        verify(appointmentRepository).deleteById(1L);
    }

    @Test
    void deleteAppointment_notFound_throwsAppointmentNotFoundException_andDoesNotDelete() {
        when(appointmentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteAppointment(99L))
                .isInstanceOf(AppointmentNotFoundException.class)
                .hasMessageContaining("99");

        verify(appointmentRepository, never()).deleteById(any());
    }
}
