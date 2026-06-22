package com.studioweb.studio_web.client;

import com.studioweb.studio_web.client.dto.ClientDtoRequest;
import com.studioweb.studio_web.client.dto.ClientDtoResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<ClientDtoResponse> getAllClients() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ClientDtoResponse getClientById(Long id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + id));
        return toResponse(client);
    }

    public ClientDtoResponse insertClient(ClientDtoRequest dto) {
        Client client = new Client();
        client.setNome(dto.nome());
        client.setTelefone(dto.telefone());
        client.setAnotacao(dto.anotacao());
        client.setCriadoEm(LocalDateTime.now());
        return toResponse(repository.save(client));
    }

    public ClientDtoResponse updateClient(Long id, ClientDtoRequest dto) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + id));
        client.setNome(dto.nome());
        client.setTelefone(dto.telefone());
        client.setAnotacao(dto.anotacao());
        return toResponse(repository.save(client));
    }

    public void deleteClient(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private ClientDtoResponse toResponse(Client client) {
        return new ClientDtoResponse(
                client.getId(),
                client.getNome(),
                client.getTelefone(),
                client.getAnotacao(),
                client.getCriadoEm()
        );
    }
}
