package com.studioweb.studio_web.appointment;

import com.studioweb.studio_web.appointment.dto.AgendamentoDtoRequest;
import com.studioweb.studio_web.appointment.dto.AgendamentoDtoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/agendamento")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoDtoResponse>> todosAgendamentos(){
        List<AgendamentoDtoResponse> response = agendamentoService.getAllAppointments();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoDtoResponse> buscarAgendamento(@PathVariable Long id){
        AgendamentoDtoResponse response = agendamentoService.getAppointmentById(id);
        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/inserir")
    public ResponseEntity<AgendamentoDtoResponse> inserirAgendamento(@RequestBody AgendamentoDtoRequest dto){
        AgendamentoDtoResponse response = agendamentoService.insertAppointment(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<AgendamentoDtoResponse> atualizarAgendamento(@PathVariable Long id, @RequestBody AgendamentoDtoRequest dto){
        AgendamentoDtoResponse response = agendamentoService.updateAppointment(id,dto);
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("status/{id}")
    public ResponseEntity<AgendamentoDtoResponse> atualizarStatus(@PathVariable Long id, @RequestBody AgendamentoStatus status){
        AgendamentoDtoResponse response = agendamentoService.updateStatus(id,status);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable Long id){
        agendamentoService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
