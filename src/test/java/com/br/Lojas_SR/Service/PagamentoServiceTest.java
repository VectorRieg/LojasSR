package com.br.Lojas_SR.Service;

import com.br.Lojas_SR.Entity.Pagamento;
import com.br.Lojas_SR.Entity.Pedido;
import com.br.Lojas_SR.Entity.StatusPagamento;
import com.br.Lojas_SR.Entity.MetodoPagamento;
import com.br.Lojas_SR.Repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Pagamento pagamento;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(1L);

        pagamento = new Pagamento();
        pagamento.setId(1L);
        pagamento.setPedido(pedido);
        pagamento.setMetodo(MetodoPagamento.CARTAO_CREDITO);
        pagamento.setValor(new BigDecimal("3000.00"));
        pagamento.setStatus(StatusPagamento.PENDENTE);
    }

    @Test
    void deveCriarPagamentoComSucesso() {
        when(pagamentoRepository.existsByPedidoId(1L)).thenReturn(false);
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        Pagamento resultado = pagamentoService.criar(pagamento);

        assertNotNull(resultado);
        assertEquals(StatusPagamento.PENDENTE, resultado.getStatus());
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    void deveLancarExcecaoQuandoPedidoJaTemPagamento() {
        when(pagamentoRepository.existsByPedidoId(1L)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pagamentoService.criar(pagamento);
        });

        assertEquals("Pedido já possui um pagamento", exception.getMessage());
    }

    @Test
    void deveBuscarPagamentoPorId() {
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));

        Pagamento resultado = pagamentoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(pagamentoRepository, times(1)).findById(1L);
    }

    @Test
    void deveBuscarPagamentoPorPedido() {
        when(pagamentoRepository.findByPedidoId(1L)).thenReturn(Optional.of(pagamento));

        Pagamento resultado = pagamentoService.buscarPorPedido(1L);

        assertNotNull(resultado);
        assertEquals(pedido, resultado.getPedido());
        verify(pagamentoRepository, times(1)).findByPedidoId(1L);
    }

    @Test
    void deveConfirmarPagamento() {
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        Pagamento resultado = pagamentoService.confirmar(1L);

        assertNotNull(resultado);
        assertEquals(StatusPagamento.APROVADO, resultado.getStatus());
        assertNotNull(resultado.getDataConfirmacao());
        verify(pagamentoRepository, times(1)).save(pagamento);
    }

    @Test
    void deveLancarExcecaoQuandoPagamentoJaConfirmado() {
        pagamento.setStatus(StatusPagamento.APROVADO);
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pagamentoService.confirmar(1L);
        });

        assertEquals("Pagamento já foi confirmado", exception.getMessage());
    }

    @Test
    void deveCancelarPagamento() {
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        Pagamento resultado = pagamentoService.cancelar(1L);

        assertNotNull(resultado);
        assertEquals(StatusPagamento.CANCELADO, resultado.getStatus());
        verify(pagamentoRepository, times(1)).save(pagamento);
    }

    @Test
    void deveAtualizarStatus() {
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        Pagamento resultado = pagamentoService.atualizarStatus(1L, "APROVADO");

        assertNotNull(resultado);
        assertEquals(StatusPagamento.APROVADO, resultado.getStatus());
        verify(pagamentoRepository, times(1)).save(pagamento);
    }

    @Test
    void deveProcessarPagamento() {
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        Pagamento resultado = pagamentoService.processar(1L);

        assertNotNull(resultado);
        assertEquals(StatusPagamento.APROVADO, resultado.getStatus());
        verify(pagamentoRepository, times(2)).save(pagamento);
    }

    @Test
    void deveListarPagamentosPendentes() {
        List<Pagamento> pagamentos = Arrays.asList(pagamento);
        when(pagamentoRepository.findPagamentosPendentes()).thenReturn(pagamentos);

        List<Pagamento> resultado = pagamentoService.listarPendentes();

        assertEquals(1, resultado.size());
        verify(pagamentoRepository, times(1)).findPagamentosPendentes();
    }

    @Test
    void deveDeletarPagamento() {
        when(pagamentoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pagamentoRepository).deleteById(1L);

        pagamentoService.deletar(1L);

        verify(pagamentoRepository, times(1)).deleteById(1L);
    }
}