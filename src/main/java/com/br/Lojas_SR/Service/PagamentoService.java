package com.br.Lojas_SR.Service;

import com.br.Lojas_SR.Entity.Pagamento;
import com.br.Lojas_SR.Entity.StatusPagamento;
import com.br.Lojas_SR.Repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    // Criar pagamento
    public Pagamento criar(Pagamento pagamento) {
        // Validações básicas
        if (pagamento.getPedido() == null) {
            throw new RuntimeException("Pedido é obrigatório");
        }
        if (pagamento.getMetodo() == null) {
            throw new RuntimeException("Método de pagamento é obrigatório");
        }
        if (pagamento.getValor() == null) {
            throw new RuntimeException("Valor é obrigatório");
        }

        // Verificar se pedido já tem pagamento
        if (pagamentoRepository.existsByPedidoId(pagamento.getPedido().getId())) {
            throw new RuntimeException("Pedido já possui um pagamento");
        }

        // Configurar dados iniciais
        pagamento.setStatus(StatusPagamento.PENDENTE);
        pagamento.setDataPagamento(LocalDateTime.now());

        return pagamentoRepository.save(pagamento);
    }

    // Buscar pagamento por ID
    public Pagamento buscarPorId(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
    }

    // Buscar pagamento por pedido
    public Pagamento buscarPorPedido(Long pedidoId) {
        return pagamentoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado para este pedido"));
    }

    // Listar pagamentos por usuário
    public List<Pagamento> listarPorUsuario(Long usuarioId) {
        return pagamentoRepository.findByUsuarioId(usuarioId);
    }

    // Listar todos os pagamentos
    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAll();
    }

    // Listar pagamentos pendentes
    public List<Pagamento> listarPendentes() {
        return pagamentoRepository.findPagamentosPendentes();
    }

    // Confirmar pagamento
    public Pagamento confirmar(Long id) {
        Pagamento pagamento = buscarPorId(id);

        if (pagamento.getStatus() == StatusPagamento.APROVADO) {
            throw new RuntimeException("Pagamento já foi confirmado");
        }

        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamento.setDataConfirmacao(LocalDateTime.now());

        return pagamentoRepository.save(pagamento);
    }

    // Cancelar pagamento
    public Pagamento cancelar(Long id) {
        Pagamento pagamento = buscarPorId(id);

        if (pagamento.getStatus() == StatusPagamento.APROVADO) {
            throw new RuntimeException("Não é possível cancelar pagamento aprovado");
        }

        pagamento.setStatus(StatusPagamento.CANCELADO);

        return pagamentoRepository.save(pagamento);
    }

    // Atualizar status
    public Pagamento atualizarStatus(Long id, String novoStatus) {
        Pagamento pagamento = buscarPorId(id);

        try {
            StatusPagamento status = StatusPagamento.valueOf(novoStatus.toUpperCase());
            pagamento.setStatus(status);

            if (status == StatusPagamento.APROVADO) {
                pagamento.setDataConfirmacao(LocalDateTime.now());
            }

            return pagamentoRepository.save(pagamento);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Status inválido: " + novoStatus);
        }
    }

    // Deletar pagamento
    public void deletar(Long id) {
        if (!pagamentoRepository.existsById(id)) {
            throw new RuntimeException("Pagamento não encontrado");
        }
        pagamentoRepository.deleteById(id);
    }

    public Pagamento processar(Long id) {
        Pagamento pagamento = buscarPorId(id);

        // Marcar como processando
        pagamento.setStatus(StatusPagamento.PROCESSANDO);
        pagamentoRepository.save(pagamento);

        // Aprovar pagamento
        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamento.setDataConfirmacao(LocalDateTime.now());

        return pagamentoRepository.save(pagamento);
    }
}
