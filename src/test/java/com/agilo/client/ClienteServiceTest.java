package com.agilo.client;

import com.agilo.client.dto.ClienteDtoRequest;
import com.agilo.client.dto.ClienteDtoResponse;
import com.agilo.exception.ClientNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setTelefone("11999990000");
        cliente.setAnotacao("prefere manhãs");
        cliente.setCriadoEm(LocalDateTime.of(2026, 6, 22, 10, 0));
    }

    @Test
    void getAllClients_returnsMappedList() {
        when(repository.findAll()).thenReturn(List.of(cliente));

        List<ClienteDtoResponse> result = service.getAllClients();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).nome()).isEqualTo("João");
        assertThat(result.get(0).telefone()).isEqualTo("11999990000");
        assertThat(result.get(0).anotacao()).isEqualTo("prefere manhãs");
    }

    @Test
    void getAllClients_emptyRepository_returnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.getAllClients()).isEmpty();
    }

    @Test
    void getClientById_found_returnsResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteDtoResponse result = service.getClientById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nome()).isEqualTo("João");
        assertThat(result.criadoEm()).isEqualTo(LocalDateTime.of(2026, 6, 22, 10, 0));
    }

    @Test
    void getClientById_notFound_throwsClientNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getClientById(99L))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void insertClient_setCriadoEm_returnsResponse() {
        ClienteDtoRequest dto = new ClienteDtoRequest("João", "11999990000", "prefere manhãs");
        when(repository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteDtoResponse result = service.insertClient(dto);

        assertThat(result.nome()).isEqualTo("João");

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCriadoEm()).isNotNull();
        assertThat(captor.getValue().getNome()).isEqualTo("João");
        assertThat(captor.getValue().getTelefone()).isEqualTo("11999990000");
        assertThat(captor.getValue().getAnotacao()).isEqualTo("prefere manhãs");
    }

    @Test
    void updateClient_found_updatesAllFields() {
        ClienteDtoRequest dto = new ClienteDtoRequest("NovoNome", "21988880000", "nova anotação");
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.save(any(Cliente.class))).thenReturn(cliente);

        service.updateClient(1L, dto);

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(repository).save(captor.capture());
        Cliente saved = captor.getValue();
        assertThat(saved.getNome()).isEqualTo("NovoNome");
        assertThat(saved.getTelefone()).isEqualTo("21988880000");
        assertThat(saved.getAnotacao()).isEqualTo("nova anotação");
    }

    @Test
    void updateClient_notFound_throwsClientNotFoundException() {
        ClienteDtoRequest dto = new ClienteDtoRequest("Nome", "11999990000", "");
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateClient(99L, dto))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteClient_exists_deletesSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteClient(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteClient_notFound_throwsClientNotFoundException_andDoesNotDelete() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteClient(99L))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).deleteById(any());
    }
}
