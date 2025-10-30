package com.br.Lojas_SR.Controller;

import com.br.Lojas_SR.Entity.Pagamento;
import com.br.Lojas_SR.Service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
@CrossOrigin(origins = "*")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping
    public ResponseEntity<Pagamento> criar(@RequestBody Pagamento pagamento) {
        Pagamento novoPagamento = pagamentoService.criar(pagamento);
        return ResponseEntity.ok(novoPagamento);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPorId(@PathVariable Long id) {
        Pagamento pagamento = pagamentoService.buscarPorId(id);
        return ResponseEntity.ok(pagamento);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Pagamento> buscarPorPedido(@PathVariable Long pedidoId) {
        Pagamento pagamento = pagamentoService.buscarPorPedido(pedidoId);
        return ResponseEntity.ok(pagamento);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pagamento>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<Pagamento> pagamentos = pagamentoService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(pagamentos);
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Pagamento> confirmar(@PathVariable Long id) {
        Pagamento pagamento = pagamentoService.confirmar(id);
        return ResponseEntity.ok(pagamento);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Pagamento> cancelar(@PathVariable Long id) {
        Pagamento pagamento = pagamentoService.cancelar(id);
        return ResponseEntity.ok(pagamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pagamento> atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusRequest request) {
        Pagamento pagamento = pagamentoService.atualizarStatus(id, request.getStatus());
        return ResponseEntity.ok(pagamento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pagamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    public static class AtualizarStatusRequest {
        private String status;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}