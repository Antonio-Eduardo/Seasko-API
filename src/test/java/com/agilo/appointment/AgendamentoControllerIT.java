package com.agilo.appointment;

import com.agilo.client.Cliente;
import com.agilo.client.ClienteRepository;
import com.agilo.user.Usuario;
import com.agilo.user.UsuarioRepository;
import com.agilo.user.UsuarioRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AgendamentoControllerIT {

    @Autowired MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired AgendamentoRepository agendamentoRepository;
    @Autowired ClienteRepository clienteRepository;
    @Autowired UsuarioRepository usuarioRepository;

    private Cliente cliente;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        agendamentoRepository.deleteAll();
        clienteRepository.deleteAll();
        usuarioRepository.deleteAll();

        cliente = new Cliente();
        cliente.setNome("Ana");
        cliente.setTelefone("11999990000");
        cliente.setAnotacao("");
        cliente.setCriadoEm(LocalDateTime.now());
        clienteRepository.save(cliente);

        usuario = new Usuario();
        usuario.setNome("Func");
        usuario.setUsuario("func");
        usuario.setSenha("hash");
        usuario.setRole(UsuarioRole.EMPLOYEE);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    @Test
    @WithMockUser
    void getAll_returnsOk() throws Exception {
        mvc.perform(get("/agendamento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mvc.perform(get("/agendamento"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void insert_validRequest_returns201WithNames() throws Exception {
        String body = """
            {
              "dataMarcada": "2026-08-01",
              "horaInicio": "09:00",
              "horaFim": "10:00",
              "clientId": %d,
              "userId": %d,
              "descricao": "Corte",
              "anotacao": ""
            }
            """.formatted(cliente.getId(), usuario.getId());

        mvc.perform(post("/agendamento/inserir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientNome").value("Ana"))
                .andExpect(jsonPath("$.userNome").value("Func"))
                .andExpect(jsonPath("$.status").value("MARCADO"));
    }

    @Test
    @WithMockUser
    void insert_missingRequiredField_returns400() throws Exception {
        String body = """
            {
              "horaInicio": "09:00",
              "horaFim": "10:00",
              "clientId": %d,
              "userId": %d,
              "descricao": "Corte",
              "anotacao": ""
            }
            """.formatted(cliente.getId(), usuario.getId());

        mvc.perform(post("/agendamento/inserir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("dataMarcada")));
    }

    @Test
    @WithMockUser
    void insert_conflictingSchedule_returns409() throws Exception {
        Agendamento existing = new Agendamento();
        existing.setClient(cliente);
        existing.setUser(usuario);
        existing.setDescricao("Existente");
        existing.setAnotacao("");
        existing.setDataMarcada(LocalDate.of(2026, 8, 1));
        existing.setHoraInicio(LocalTime.of(9, 0));
        existing.setHoraFim(LocalTime.of(10, 0));
        existing.setStatus(AgendamentoStatus.MARCADO);
        existing.setCriadoEm(LocalDateTime.now());
        agendamentoRepository.save(existing);

        String body = """
            {
              "dataMarcada": "2026-08-01",
              "horaInicio": "09:30",
              "horaFim": "10:30",
              "clientId": %d,
              "userId": %d,
              "status": "MARCADO",
              "descricao": "Conflito",
              "anotacao": ""
            }
            """.formatted(cliente.getId(), usuario.getId());

        mvc.perform(post("/agendamento/inserir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void getById_notFound_returns404() throws Exception {
        mvc.perform(get("/agendamento/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void update_existingAppointment_returns200() throws Exception {
        Agendamento ag = new Agendamento();
        ag.setClient(cliente);
        ag.setUser(usuario);
        ag.setDescricao("Original");
        ag.setAnotacao("");
        ag.setDataMarcada(LocalDate.of(2026, 8, 1));
        ag.setHoraInicio(LocalTime.of(9, 0));
        ag.setHoraFim(LocalTime.of(10, 0));
        ag.setStatus(AgendamentoStatus.MARCADO);
        ag.setCriadoEm(LocalDateTime.now());
        agendamentoRepository.save(ag);

        String body = """
            {
              "dataMarcada": "2026-08-01",
              "horaInicio": "09:00",
              "horaFim": "10:00",
              "clientId": %d,
              "userId": %d,
              "status": "CONFIRMADO",
              "descricao": "Atualizado",
              "anotacao": "obs"
            }
            """.formatted(cliente.getId(), usuario.getId());

        mvc.perform(put("/agendamento/atualizar/" + ag.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Atualizado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_existing_returns204() throws Exception {
        Agendamento ag = new Agendamento();
        ag.setClient(cliente);
        ag.setUser(usuario);
        ag.setDescricao("Deletar");
        ag.setAnotacao("");
        ag.setDataMarcada(LocalDate.of(2026, 8, 2));
        ag.setHoraInicio(LocalTime.of(11, 0));
        ag.setHoraFim(LocalTime.of(12, 0));
        ag.setStatus(AgendamentoStatus.MARCADO);
        ag.setCriadoEm(LocalDateTime.now());
        agendamentoRepository.save(ag);

        mvc.perform(delete("/agendamento/deletar/" + ag.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void delete_asEmployee_returns403() throws Exception {
        mvc.perform(delete("/agendamento/deletar/1"))
                .andExpect(status().isForbidden());
    }
}
