package com.studioweb.studio_web.user;

import com.studioweb.studio_web.user.dto.UserDtoRequest;
import com.studioweb.studio_web.user.dto.UserDtoResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDtoResponse> getAllUsers() {
        return repository.findAll().stream()
                .map(user -> new UserDtoResponse(user.getId(), user.getUsuario(), user.getRole(), user.getAtivo()))
                .toList();
    }

    public UserDtoResponse getUserById(Long id) {
        User user = repository.findById(id).orElseThrow(RuntimeException::new);
        return new UserDtoResponse(user.getId(), user.getUsuario(), user.getRole(), user.getAtivo());
    }

    public UserDtoResponse insertUser(UserDtoRequest dto) {
        User user = new User();
        user.setNome(dto.nome());
        user.setUsuario(dto.usuario());
        user.setSenha(passwordEncoder.encode(dto.senha()));
        user.setRole(dto.role());
        user.setAtivo(true);
        User saved = repository.save(user);
        return new UserDtoResponse(saved.getId(), saved.getUsuario(), saved.getRole(), saved.getAtivo());
    }
}
