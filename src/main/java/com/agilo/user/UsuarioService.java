package com.agilo.user;

import com.agilo.exception.UserNotFoundException;
import com.agilo.user.dto.UsuarioDtoRequest;
import com.agilo.user.dto.UsuarioDtoResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioDtoResponse> getAllUsers() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioDtoResponse getUserById(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    public UsuarioDtoResponse insertUser(UsuarioDtoRequest dto) {
        Usuario user = new Usuario();
        user.setNome(dto.nome());
        user.setUsuario(dto.usuario());
        user.setSenha(passwordEncoder.encode(dto.senha()));
        user.setRole(dto.role());
        user.setAtivo(true);
        return toResponse(repository.save(user));
    }
    public void deleteUser(Long id){
        if (!repository.existsById(id)){
            throw new UserNotFoundException(id);
        }
        repository.deleteById(id);
    }
    @Transactional
    public UsuarioDtoResponse updateUser(Long id, UsuarioDtoRequest dto){
        Usuario usuario = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if(dto.usuario() != null) {
            usuario.setUsuario(dto.usuario());
        }
        if(dto.nome() != null) {
            usuario.setNome(dto.nome());
        }
        if (dto.senha() != null) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }
        if (dto.role() != null) {
            usuario.setRole(dto.role());
        }
        return toResponse(repository.save(usuario));
    }
    private UsuarioDtoResponse toResponse(Usuario usuario) {
        return new UsuarioDtoResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsuario(),
                usuario.getRole(),
                usuario.getAtivo());
    }
}
