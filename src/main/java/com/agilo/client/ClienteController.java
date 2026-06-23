package com.agilo.client;

import com.agilo.client.dto.ClienteDtoRequest;
import com.agilo.client.dto.ClienteDtoResponse;
import com.agilo.common.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    @GetMapping
    public ResponseEntity<PageResponse<ClienteDtoResponse>> todosClientes(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "25") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("nome").ascending());
        PageResponse<ClienteDtoResponse> response = clienteService.getAllClients(pageable);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDtoResponse> buscarCliente(@PathVariable Long id){
        ClienteDtoResponse response = clienteService.getClientById(id);
        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/inserir")
    public ResponseEntity<ClienteDtoResponse> inserirCliente(@RequestBody @Valid ClienteDtoRequest dto){
        ClienteDtoResponse response = clienteService.insertClient(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ClienteDtoResponse> atualizarCliente(@PathVariable Long id, @RequestBody @Valid ClienteDtoRequest dto){
        ClienteDtoResponse response = clienteService.updateClient(id,dto);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id){
        clienteService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
