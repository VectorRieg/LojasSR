package com.br.Lojas_SR.Controller;

import com.br.Lojas_SR.Entity.Carrinho;
import com.br.Lojas_SR.Service.CarrinhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/carrinho")
@CrossOrigin(origins = "*")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @PostMapping
    public ResponseEntity<Carrinho> criar(@RequestBody CarrinhoRequest request) {
        Carrinho carrinho = carrinhoService.criar(request.getUsuarioId());
        return ResponseEntity.ok(carrinho);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Carrinho> buscarPorUsuario(@PathVariable Long usuarioId) {
        Carrinho carrinho = carrinhoService.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(carrinho);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrinho> buscarPorId(@PathVariable Long id) {
        Carrinho carrinho = carrinhoService.buscarPorId(id);
        return ResponseEntity.ok(carrinho);
    }

    @GetMapping("/{id}/total")
    public ResponseEntity<BigDecimal> calcularTotal(@PathVariable Long id) {
        BigDecimal total = carrinhoService.calcularTotal(id);
        return ResponseEntity.ok(total);
    }

    @DeleteMapping("/{id}/limpar")
    public ResponseEntity<Void> limpar(@PathVariable Long id) {
        carrinhoService.limpar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        carrinhoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    public static class CarrinhoRequest {
        private Long usuarioId;

        public Long getUsuarioId() { return usuarioId; }
        public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    }
}