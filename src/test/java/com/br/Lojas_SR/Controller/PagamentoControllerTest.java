package com.br.Lojas_SR.Controller;

import com.br.Lojas_SR.Entity.Pagamento;
import com.br.Lojas_SR.Entity.StatusPagamento;
import com.br.Lojas_SR.Service.PagamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagamentoController.class)
class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PagamentoService pagamentoService;

    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        pagamento = new Pagamento();
        pagamento.setId(1L);
        pagamento.setValor(new BigDecimal("100.00"));
        pagamento.setStatus(StatusPagamento.PENDENTE);
    }

    @Test
    void deveCriarPagamentoComSucesso() throws Exception {
        when(pagamentoService.criar(any(Pagamento.class))).thenReturn(pagamento);

        mockMvc.perform(post("/api/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(pagamentoService, times(1)).criar(any(Pagamento.class));
    }

    @Test
    void deveBuscarPagamentoPorId() throws Exception {
        when(pagamentoService.buscarPorId(1L)).thenReturn(pagamento);

        mockMvc.perform(get("/api/pagamentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(pagamentoService, times(1)).buscarPorId(1L);
    }

    @Test
    void deveBuscarPagamentoPorPedido() throws Exception {
        when(pagamentoService.buscarPorPedido(1L)).thenReturn(pagamento);

        mockMvc.perform(get("/api/pagamentos/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(pagamentoService, times(1)).buscarPorPedido(1L);
    }

    @Test
    void deveListarPagamentosPorUsuario() throws Exception {
        List<Pagamento> pagamentos = Arrays.asList(pagamento);
        when(pagamentoService.listarPorUsuario(1L)).thenReturn(pagamentos);

        mockMvc.perform(get("/api/pagamentos/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(pagamentoService, times(1)).listarPorUsuario(1L);
    }

    @Test
    void deveConfirmarPagamento() throws Exception {
        pagamento.setStatus(StatusPagamento.APROVADO);
        when(pagamentoService.confirmar(1L)).thenReturn(pagamento);

        mockMvc.perform(put("/api/pagamentos/1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"));

        verify(pagamentoService, times(1)).confirmar(1L);
    }

    @Test
    void deveCancelarPagamento() throws Exception {
        pagamento.setStatus(StatusPagamento.CANCELADO);
        when(pagamentoService.cancelar(1L)).thenReturn(pagamento);

        mockMvc.perform(put("/api/pagamentos/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));

        verify(pagamentoService, times(1)).cancelar(1L);
    }

    @Test
    void deveAtualizarStatus() throws Exception {
        PagamentoController.AtualizarStatusRequest request = new PagamentoController.AtualizarStatusRequest();
        request.setStatus("APROVADO");

        pagamento.setStatus(StatusPagamento.APROVADO);
        when(pagamentoService.atualizarStatus(anyLong(), anyString())).thenReturn(pagamento);

        mockMvc.perform(put("/api/pagamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"));

        verify(pagamentoService, times(1)).atualizarStatus(anyLong(), anyString());
    }

    @Test
    void deveDeletarPagamento() throws Exception {
        doNothing().when(pagamentoService).deletar(1L);

        mockMvc.perform(delete("/api/pagamentos/1"))
                .andExpect(status().isNoContent());

        verify(pagamentoService, times(1)).deletar(1L);
    }
}