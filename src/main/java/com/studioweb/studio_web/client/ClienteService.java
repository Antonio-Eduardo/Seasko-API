package com.studioweb.studio_web.client;

import com.studioweb.studio_web.client.dto.ClienteDtoRequest;
import com.studioweb.studio_web.client.dto.ClienteDtoResponse;
import com.studioweb.studio_web.exception.ClientNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public List<ClienteDtoResponse> getAllClients() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ClienteDtoResponse getClientById(Long id) {
        Cliente client = repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        return toResponse(client);
    }

    public ClienteDtoResponse insertClient(ClienteDtoRequest dto) {
        Cliente client = new Cliente();
        client.setNome(dto.nome());
        client.setTelefone(dto.telefone());
        client.setAnotacao(dto.anotacao());
        client.setCriadoEm(LocalDateTime.now());
        return toResponse(repository.save(client));
    }

    @Transactional
    public ClienteDtoResponse updateClient(Long id, ClienteDtoRequest dto) {
        Cliente client = repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        client.setNome(dto.nome());
        client.setTelefone(dto.telefone());
        client.setAnotacao(dto.anotacao());
        return toResponse(repository.save(client));
    }

    public void deleteClient(Long id) {
        if (!repository.existsById(id)) {
            throw new ClientNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private ClienteDtoResponse toResponse(Cliente client) {
        return new ClienteDtoResponse(
                client.getId(),
                client.getNome(),
                client.getTelefone(),
                client.getAnotacao(),
                client.getCriadoEm()
        );
    }
}
