package com.agilo.client;

import com.agilo.appointment.AgendamentoRepository;
import com.agilo.user.UsuarioRepository;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerIT {

    @Autowired MockMvc mvc;
    @Autowired ClienteRepository clienteRepository;
    @Autowired AgendamentoRepository agendamentoRepository;
    @Autowired UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        agendamentoRepository.deleteAll();
        clienteRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void getAll_returnsOkWithContent() throws Exception {
        Cliente c = new Cliente();
        c.setNome("Carlos");
        c.setTelefone("11988880000");
        c.setAnotacao("");
        c.setCriadoEm(LocalDateTime.now());
        clienteRepository.save(c);

        mvc.perform(get("/cliente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Carlos"));
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mvc.perform(get("/cliente"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void insert_validRequest_returns201() throws Exception {
        String body = """
            {"nome": "Beatriz", "telefone": "21977770000", "anotacao": ""}
            """;

        mvc.perform(post("/cliente/inserir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Beatriz"))
                .andExpect(jsonPath("$.telefone").value("21977770000"));
    }

    @Test
    @WithMockUser
    void insert_blankNome_returns400() throws Exception {
        String body = """
            {"nome": "", "telefone": "21977770000", "anotacao": ""}
            """;

        mvc.perform(post("/cliente/inserir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("nome")));
    }

    @Test
    @WithMockUser
    void update_notFound_returns404() throws Exception {
        String body = """
            {"nome": "X", "telefone": "11900000000", "anotacao": ""}
            """;

        mvc.perform(put("/cliente/atualizar/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_existing_returns204() throws Exception {
        Cliente c = new Cliente();
        c.setNome("Deletar");
        c.setTelefone("11911110000");
        c.setAnotacao("");
        c.setCriadoEm(LocalDateTime.now());
        clienteRepository.save(c);

        mvc.perform(delete("/cliente/deletar/" + c.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void delete_asEmployee_returns403() throws Exception {
        mvc.perform(delete("/cliente/deletar/1"))
                .andExpect(status().isForbidden());
    }
}
