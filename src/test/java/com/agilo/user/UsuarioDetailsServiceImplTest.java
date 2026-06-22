package com.agilo.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceImplTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioDetailsServiceImpl service;

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
    void loadUserByUsername_found_returnsUserDetails() {
        when(repository.findByUsuario("maria")).thenReturn(Optional.of(usuario));

        UserDetails result = service.loadUserByUsername("maria");

        assertThat(result.getUsername()).isEqualTo("maria");
        assertThat(result.getPassword()).isEqualTo("encodedSenha");
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_EMPLOYEE");
    }

    @Test
    void loadUserByUsername_notFound_throwsUsernameNotFoundException() {
        when(repository.findByUsuario("desconhecido")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("desconhecido"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("desconhecido");
    }

    @Test
    void loadUserByUsername_adminRole_returnsAdminAuthority() {
        usuario.setRole(UsuarioRole.ADMIN);
        when(repository.findByUsuario("maria")).thenReturn(Optional.of(usuario));

        UserDetails result = service.loadUserByUsername("maria");

        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_inactiveUser_isEnabledFalse() {
        usuario.setAtivo(false);
        when(repository.findByUsuario("maria")).thenReturn(Optional.of(usuario));

        UserDetails result = service.loadUserByUsername("maria");

        assertThat(result.isEnabled()).isFalse();
    }
}
