package com.br.Lojas_SR.Controller;

import com.br.Lojas_SR.Entity.Carrinho;
import com.br.Lojas_SR.Service.CarrinhoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.ArrayList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarrinhoController.class)
class CarrinhoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarrinhoService carrinhoService;

    private Carrinho carrinho;

    @BeforeEach
    void setUp() {
        carrinho = new Carrinho();
        carrinho.setId(1L);
        carrinho.setItens(new ArrayList<>());
    }

    @Test
    void deveCriarCarrinhoComSucesso() throws Exception {
        CarrinhoController.CarrinhoRequest request = new CarrinhoController.CarrinhoRequest();
        request.setUsuarioId(1L);

        when(carrinhoService.criar(anyLong())).thenReturn(carrinho);

        mockMvc.perform(post("/api/carrinho")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(carrinhoService, times(1)).criar(anyLong());
    }

    @Test
    void deveBuscarCarrinhoPorUsuario() throws Exception {
        when(carrinhoService.buscarPorUsuario(1L)).thenReturn(carrinho);

        mockMvc.perform(get("/api/carrinho/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(carrinhoService, times(1)).buscarPorUsuario(1L);
    }

    @Test
    void deveBuscarCarrinhoPorId() throws Exception {
        when(carrinhoService.buscarPorId(1L)).thenReturn(carrinho);

        mockMvc.perform(get("/api/carrinho/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(carrinhoService, times(1)).buscarPorId(1L);
    }

    @Test
    void deveCalcularTotal() throws Exception {
        when(carrinhoService.calcularTotal(1L)).thenReturn(new BigDecimal("100.00"));

        mockMvc.perform(get("/api/carrinho/1/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("100.00"));

        verify(carrinhoService, times(1)).calcularTotal(1L);
    }

    @Test
    void deveLimparCarrinho() throws Exception {
        doNothing().when(carrinhoService).limpar(1L);

        mockMvc.perform(delete("/api/carrinho/1/limpar"))
                .andExpect(status().isNoContent());

        verify(carrinhoService, times(1)).limpar(1L);
    }

    @Test
    void deveDeletarCarrinho() throws Exception {
        doNothing().when(carrinhoService).deletar(1L);

        mockMvc.perform(delete("/api/carrinho/1"))
                .andExpect(status().isNoContent());

        verify(carrinhoService, times(1)).deletar(1L);
    }
}