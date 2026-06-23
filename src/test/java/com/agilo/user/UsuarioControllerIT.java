package com.agilo.user;

import com.agilo.appointment.AgendamentoRepository;
import com.agilo.client.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UsuarioControllerIT {

    @Autowired MockMvc mvc;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired AgendamentoRepository agendamentoRepository;
    @Autowired ClienteRepository clienteRepository;

    @BeforeEach
    void setUp() {
        agendamentoRepository.deleteAll();
        clienteRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void getAll_returnsOk() throws Exception {
        mvc.perform(get("/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mvc.perform(get("/usuario"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void insert_validRequest_returns201() throws Exception {
        String body = """
            {"nome": "Maria", "usuario": "maria", "senha": "senha123", "role": "EMPLOYEE"}
            """;

        mvc.perform(post("/usuario/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario").value("maria"))
                .andExpect(jsonPath("$.role").value("EMPLOYEE"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void insert_asEmployee_returns403() throws Exception {
        String body = """
            {"nome": "X", "usuario": "x", "senha": "x", "role": "EMPLOYEE"}
            """;

        mvc.perform(post("/usuario/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void insert_blankUsuario_returns400() throws Exception {
        String body = """
            {"nome": "Maria", "usuario": "", "senha": "senha123", "role": "EMPLOYEE"}
            """;

        mvc.perform(post("/usuario/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("usuario")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_notFound_returns404() throws Exception {
        mvc.perform(get("/usuario/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_existing_returns204() throws Exception {
        Usuario u = new Usuario();
        u.setNome("Temp");
        u.setUsuario("temp");
        u.setSenha("hash");
        u.setRole(UsuarioRole.EMPLOYEE);
        u.setAtivo(true);
        usuarioRepository.save(u);

        mvc.perform(delete("/usuario/deletar/" + u.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void delete_asEmployee_returns403() throws Exception {
        mvc.perform(delete("/usuario/deletar/1"))
                .andExpect(status().isForbidden());
    }
}
