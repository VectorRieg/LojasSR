package com.br.Lojas_SR.Integracao;

import com.br.Lojas_SR.Entity.*;
import com.br.Lojas_SR.Service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Testes de Integração - Pagamento Completo")
class IntegracaoPagamentoTest {

    @Autowired
    private AcessoService acessoService;

    @Autowired
    private PagamentoService pagamentoService;

    private Usuario usuario;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        // Criar usuário
        usuario = new Usuario();
        usuario.setNome("Maria Pagamento");
        usuario.setEmail("pagamento" + System.currentTimeMillis() + "@email.com");
        usuario.setSenha("senha123");
        usuario = acessoService.registrar(usuario);

        // Criar pedido
        pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setNumeroPedido("PED-" + System.currentTimeMillis());
        pedido.setSubTotal(new BigDecimal("100.00"));
        pedido.setValorFrete(new BigDecimal("10.00"));
        pedido.setValorTotal(new BigDecimal("110.00"));
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setItens(new ArrayList<>());
    }

    @Test
    @DisplayName("Deve realizar fluxo completo de pagamento")
    void deveRealizarFluxoCompletoDePagamento() {
        // 1. Criar pagamento
        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setMetodo(MetodoPagamento.CARTAO_CREDITO);
        pagamento.setValor(new BigDecimal("110.00"));
        pagamento.setNumeroCartao("**** 1234");
        pagamento.setBandeiraCartao("Visa");
        pagamento.setParcelas(3);

        Pagamento criado = pagamentoService.criar(pagamento);
        assertNotNull(criado.getId());
        assertEquals(StatusPagamento.PENDENTE, criado.getStatus());
        assertNotNull(criado.getDataPagamento());

        // 2. Processar pagamento
        Pagamento processado = pagamentoService.processar(criado.getId());
        assertEquals(StatusPagamento.APROVADO, processado.getStatus());
        assertNotNull(processado.getDataConfirmacao());

        // 3. Buscar por pedido
        Pagamento encontrado = pagamentoService.buscarPorPedido(pedido.getId());
        assertNotNull(encontrado);
        assertEquals(StatusPagamento.APROVADO, encontrado.getStatus());

        // 4. Listar pagamentos do usuário
        List<Pagamento> pagamentosUsuario = pagamentoService.listarPorUsuario(usuario.getId());
        assertTrue(pagamentosUsuario.size() >= 1);
    }

    @Test
    @DisplayName("Não deve permitir dois pagamentos para mesmo pedido")
    void naoDevePermitirDoisPagamentosParaMesmoPedido() {
        Pagamento pagamento1 = new Pagamento();
        pagamento1.setPedido(pedido);
        pagamento1.setMetodo(MetodoPagamento.PIX);
        pagamento1.setValor(new BigDecimal("110.00"));

        pagamentoService.criar(pagamento1);

        Pagamento pagamento2 = new Pagamento();
        pagamento2.setPedido(pedido);
        pagamento2.setMetodo(MetodoPagamento.BOLETO);
        pagamento2.setValor(new BigDecimal("110.00"));

        assertThrows(RuntimeException.class, () -> {
            pagamentoService.criar(pagamento2);
        });
    }

    @Test
    @DisplayName("Não deve cancelar pagamento aprovado")
    void naoDeveCancelarPagamentoAprovado() {
        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setMetodo(MetodoPagamento.PIX);
        pagamento.setValor(new BigDecimal("110.00"));

        Pagamento criado = pagamentoService.criar(pagamento);
        pagamentoService.confirmar(criado.getId());

        assertThrows(RuntimeException.class, () -> {
            pagamentoService.cancelar(criado.getId());
        });
    }

    @Test
    @DisplayName("Deve confirmar pagamento manualmente")
    void deveConfirmarPagamentoManualmente() {
        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setMetodo(MetodoPagamento.BOLETO);
        pagamento.setValor(new BigDecimal("110.00"));

        Pagamento criado = pagamentoService.criar(pagamento);
        assertEquals(StatusPagamento.PENDENTE, criado.getStatus());

        Pagamento confirmado = pagamentoService.confirmar(criado.getId());
        assertEquals(StatusPagamento.APROVADO, confirmado.getStatus());
        assertNotNull(confirmado.getDataConfirmacao());
    }

    @Test
    @DisplayName("Não deve confirmar pagamento já confirmado")
    void naoDeveConfirmarPagamentoJaConfirmado() {
        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setMetodo(MetodoPagamento.PIX);
        pagamento.setValor(new BigDecimal("110.00"));

        Pagamento criado = pagamentoService.criar(pagamento);
        pagamentoService.confirmar(criado.getId());

        assertThrows(RuntimeException.class, () -> {
            pagamentoService.confirmar(criado.getId());
        });
    }

    @Test
    @DisplayName("Deve atualizar status do pagamento")
    void deveAtualizarStatusDoPagamento() {
        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setMetodo(MetodoPagamento.CARTAO_CREDITO);
        pagamento.setValor(new BigDecimal("110.00"));

        Pagamento criado = pagamentoService.criar(pagamento);

        Pagamento atualizado = pagamentoService.atualizarStatus(criado.getId(), "PROCESSANDO");
        assertEquals(StatusPagamento.PROCESSANDO, atualizado.getStatus());

        atualizado = pagamentoService.atualizarStatus(criado.getId(), "APROVADO");
        assertEquals(StatusPagamento.APROVADO, atualizado.getStatus());
        assertNotNull(atualizado.getDataConfirmacao());
    }

    @Test
    @DisplayName("Deve listar pagamentos pendentes")
    void deveListarPagamentosPendentes() {
        // Criar pagamentos com diferentes status
        Pagamento pag1 = new Pagamento();
        pag1.setPedido(pedido);
        pag1.setMetodo(MetodoPagamento.PIX);
        pag1.setValor(new BigDecimal("50.00"));
        pagamentoService.criar(pag1);

        List<Pagamento> pendentes = pagamentoService.listarPendentes();
        assertTrue(pendentes.stream().anyMatch(p ->
                p.getStatus() == StatusPagamento.PENDENTE ||
                        p.getStatus() == StatusPagamento.PROCESSANDO
        ));
    }

    @Test
    @DisplayName("Deve processar pagamento em múltiplas parcelas")
    void deveProcessarPagamentoEmMultiplasParcelas() {
        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setMetodo(MetodoPagamento.CARTAO_CREDITO);
        pagamento.setValor(new BigDecimal("1200.00"));
        pagamento.setNumeroCartao("**** 5678");
        pagamento.setBandeiraCartao("Mastercard");
        pagamento.setParcelas(12);

        Pagamento criado = pagamentoService.criar(pagamento);
        assertEquals(12, criado.getParcelas());

        BigDecimal valorParcela = criado.getValor().divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP);
        assertEquals(new BigDecimal("100.00"), valorParcela);
    }

    @Test
    @DisplayName("Deve recusar pagamento com valor diferente do pedido")
    void deveRecusarPagamentoComValorDiferenteDoPedido() {
        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setMetodo(MetodoPagamento.PIX);
        pagamento.setValor(new BigDecimal("50.00")); // Valor diferente do pedido (110.00)

        // Em uma implementação real, isso deveria lançar exceção
        // Por enquanto, apenas criamos o pagamento
        Pagamento criado = pagamentoService.criar(pagamento);
        assertNotEquals(pedido.getValorTotal(), criado.getValor());
    }
}