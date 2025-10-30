package com.br.Lojas_SR.Controller;

import com.br.Lojas_SR.Entity.Usuario;
import com.br.Lojas_SR.Service.AcessoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AcessoController.class)
class AcessoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AcessoService acessoService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senha123");
    }

    @Test
    void deveRegistrarUsuarioComSucesso() throws Exception {
        when(acessoService.registrar(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/acesso/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        verify(acessoService, times(1)).registrar(any(Usuario.class));
    }

    @Test
    void deveFazerLoginComSucesso() throws Exception {
        AcessoController.LoginRequest loginRequest = new AcessoController.LoginRequest();
        loginRequest.setEmail("joao@email.com");
        loginRequest.setSenha("senha123");

        when(acessoService.login(anyString(), anyString())).thenReturn("token-jwt-123");

        mockMvc.perform(post("/api/acesso/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("token-jwt-123"));

        verify(acessoService, times(1)).login(anyString(), anyString());
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        when(acessoService.buscarPorId(1L)).thenReturn(usuario);

        mockMvc.perform(get("/api/acesso/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(acessoService, times(1)).buscarPorId(1L);
    }

    @Test
    void deveAtualizarUsuario() throws Exception {
        when(acessoService.atualizar(anyLong(), any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(put("/api/acesso/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(acessoService, times(1)).atualizar(anyLong(), any(Usuario.class));
    }

    @Test
    void deveDeletarUsuario() throws Exception {
        doNothing().when(acessoService).deletar(1L);

        mockMvc.perform(delete("/api/acesso/1"))
                .andExpect(status().isNoContent());

        verify(acessoService, times(1)).deletar(1L);
    }
}
