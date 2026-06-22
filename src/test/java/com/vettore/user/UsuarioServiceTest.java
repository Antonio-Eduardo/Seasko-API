package com.vettore.user;

import com.vettore.exception.UserNotFoundException;
import com.vettore.user.dto.UsuarioDtoRequest;
import com.vettore.user.dto.UsuarioDtoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Maria");
        usuario.setUsuario("maria");
        usuario.setSenha("encodedSenha");
        usuario.setRole(UsuarioRole.EMPLOYEE);
        usuario.setAtivo(true);
    }

    @Test
    void getAllUsers_returnsMappedList() {
        when(repository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioDtoResponse> result = service.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).usuario()).isEqualTo("maria");
        assertThat(result.get(0).role()).isEqualTo(UsuarioRole.EMPLOYEE);
        assertThat(result.get(0).ativo()).isTrue();
    }

    @Test
    void getAllUsers_emptyRepository_returnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.getAllUsers()).isEmpty();
    }

    @Test
    void getUserById_found_returnsResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioDtoResponse result = service.getUserById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.usuario()).isEqualTo("maria");
    }

    @Test
    void getUserById_notFound_throwsUserNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void insertUser_encodesPassword_setsAtivoTrue() {
        UsuarioDtoRequest dto = new UsuarioDtoRequest("Maria", "maria", "senha123", UsuarioRole.EMPLOYEE);
        when(passwordEncoder.encode("senha123")).thenReturn("hashed");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        service.insertUser(dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getSenha()).isEqualTo("hashed");
        assertThat(captor.getValue().getAtivo()).isTrue();
    }

    @Test
    void insertUser_returnsResponseWithSavedData() {
        UsuarioDtoRequest dto = new UsuarioDtoRequest("Maria", "maria", "senha123", UsuarioRole.EMPLOYEE);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioDtoResponse result = service.insertUser(dto);

        assertThat(result.usuario()).isEqualTo("maria");
        assertThat(result.role()).isEqualTo(UsuarioRole.EMPLOYEE);
    }

    @Test
    void deleteUser_exists_deletesSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteUser(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteUser_notFound_throwsUserNotFoundException_andDoesNotDelete() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteUser(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).deleteById(any());
    }

    @Test
    void updateUser_allFields_updatesAll() {
        UsuarioDtoRequest dto = new UsuarioDtoRequest("NovoNome", "novoLogin", "novaSenha", UsuarioRole.ADMIN);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha")).thenReturn("novoHash");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        service.updateUser(1L, dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        Usuario saved = captor.getValue();
        assertThat(saved.getNome()).isEqualTo("NovoNome");
        assertThat(saved.getUsuario()).isEqualTo("novoLogin");
        assertThat(saved.getSenha()).isEqualTo("novoHash");
        assertThat(saved.getRole()).isEqualTo(UsuarioRole.ADMIN);
    }

    @Test
    void updateUser_nullFields_preservesExistingValues() {
        UsuarioDtoRequest dto = new UsuarioDtoRequest(null, null, null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        service.updateUser(1L, dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        Usuario saved = captor.getValue();
        assertThat(saved.getNome()).isEqualTo("Maria");
        assertThat(saved.getUsuario()).isEqualTo("maria");
        assertThat(saved.getSenha()).isEqualTo("encodedSenha");
        assertThat(saved.getRole()).isEqualTo(UsuarioRole.EMPLOYEE);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateUser_notFound_throwsUserNotFoundException() {
        UsuarioDtoRequest dto = new UsuarioDtoRequest("Nome", "login", "senha", UsuarioRole.ADMIN);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateUser(99L, dto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }
}
