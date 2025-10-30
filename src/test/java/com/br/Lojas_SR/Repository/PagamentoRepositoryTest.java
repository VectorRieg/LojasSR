package com.br.Lojas_SR.Repository;

import com.br.Lojas_SR.Entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PagamentoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    private Pagamento pagamento;
    private Pedido pedido;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senha");
        entityManager.persist(usuario);

        pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setNumeroPedido("PED-001");
        pedido.setSubTotal(new BigDecimal("100.00"));
        pedido.setValorFrete(new BigDecimal("10.00"));
        pedido.setValorTotal(new BigDecimal("110.00"));
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setItens(new ArrayList<>());
        entityManager.persist(pedido);

        pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setMetodo(MetodoPagamento.CARTAO_CREDITO);
        pagamento.setValor(new BigDecimal("110.00"));
        pagamento.setStatus(StatusPagamento.PENDENTE);
        pagamento.setDataPagamento(LocalDateTime.now());
    }

    @Test
    void deveSalvarPagamento() {
        Pagamento salvo = pagamentoRepository.save(pagamento);

        assertNotNull(salvo.getId());
        assertEquals(MetodoPagamento.CARTAO_CREDITO, salvo.getMetodo());
    }

    @Test
    void deveBuscarPagamentoPorPedido() {
        entityManager.persist(pagamento);
        entityManager.flush();

        Optional<Pagamento> encontrado = pagamentoRepository.findByPedidoId(pedido.getId());

        assertTrue(encontrado.isPresent());
        assertEquals(pedido.getId(), encontrado.get().getPedido().getId());
    }

    @Test
    void deveListarPagamentosPorUsuario() {
        entityManager.persist(pagamento);
        entityManager.flush();

        List<Pagamento> pagamentos = pagamentoRepository.findByUsuarioId(usuario.getId());

        assertEquals(1, pagamentos.size());
    }

    @Test
    void deveListarPorStatus() {
        entityManager.persist(pagamento);
        entityManager.flush();

        List<Pagamento> pendentes = pagamentoRepository.findByStatus(StatusPagamento.PENDENTE);

        assertEquals(1, pendentes.size());
        assertEquals(StatusPagamento.PENDENTE, pendentes.get(0).getStatus());
    }

    @Test
    void deveListarPagamentosPendentes() {
        Pagamento pagamento2 = new Pagamento();
        pagamento2.setPedido(pedido);
        pagamento2.setMetodo(MetodoPagamento.PIX);
        pagamento2.setValor(new BigDecimal("50.00"));
        pagamento2.setStatus(StatusPagamento.PROCESSANDO);
        pagamento2.setDataPagamento(LocalDateTime.now());

        Pagamento pagamento3 = new Pagamento();
        pagamento3.setPedido(pedido);
        pagamento3.setMetodo(MetodoPagamento.BOLETO);
        pagamento3.setValor(new BigDecimal("30.00"));
        pagamento3.setStatus(StatusPagamento.APROVADO);
        pagamento3.setDataPagamento(LocalDateTime.now());

        entityManager.persist(pagamento);
        entityManager.persist(pagamento2);
        entityManager.persist(pagamento3);
        entityManager.flush();

        List<Pagamento> pendentes = pagamentoRepository.findPagamentosPendentes();

        assertEquals(2, pendentes.size());
    }

    @Test
    void deveVerificarSePedidoTemPagamento() {
        entityManager.persist(pagamento);
        entityManager.flush();

        boolean existe = pagamentoRepository.existsByPedidoId(pedido.getId());

        assertTrue(existe);
    }
}