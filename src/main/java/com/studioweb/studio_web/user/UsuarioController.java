package com.studioweb.studio_web.user;

import com.studioweb.studio_web.user.dto.UsuarioDtoRequest;
import com.studioweb.studio_web.user.dto.UsuarioDtoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDtoResponse>> getAllUsers(){
        List<UsuarioDtoResponse> response = usuarioService.getAllUsers();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDtoResponse>  userById(@PathVariable Long id){
        UsuarioDtoResponse response = usuarioService.getUserById(id);
        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDtoResponse> insertUser(@RequestBody UsuarioDtoRequest dto){
        UsuarioDtoResponse response = usuarioService.insertUser(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id){
        usuarioService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<UsuarioDtoResponse> atualizarUmUsuario(@PathVariable Long id, @RequestBody UsuarioDtoRequest dto){
        UsuarioDtoResponse response = usuarioService.updateUser(id,dto);
        return ResponseEntity.ok().body(response);
    }
}
