package com.br.Lojas_SR.Controller;

import com.br.Lojas_SR.Entity.Item;
import com.br.Lojas_SR.Service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/itens")
@CrossOrigin(origins = "*")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping
    public ResponseEntity<Item> adicionar(@RequestBody ItemRequest request) {
        Item item = itemService.adicionar(
                request.getCarrinhoId(),
                request.getProdutoId(),
                request.getQuantidade()
        );
        return ResponseEntity.ok(item);
    }

    @GetMapping("/carrinho/{carrinhoId}")
    public ResponseEntity<List<Item>> listarPorCarrinho(@PathVariable Long carrinhoId) {
        List<Item> itens = itemService.listarPorCarrinho(carrinhoId);
        return ResponseEntity.ok(itens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> buscarPorId(@PathVariable Long id) {
        Item item = itemService.buscarPorId(id);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> atualizarQuantidade(@PathVariable Long id, @RequestBody AtualizarQuantidadeRequest request) {
        Item item = itemService.atualizarQuantidade(id, request.getQuantidade());
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        itemService.remover(id);
        return ResponseEntity.noContent().build();
    }

    public static class ItemRequest {
        private Long carrinhoId;
        private Long produtoId;
        private Integer quantidade;

        public Long getCarrinhoId() { return carrinhoId; }
        public void setCarrinhoId(Long carrinhoId) { this.carrinhoId = carrinhoId; }
        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    }

    public static class AtualizarQuantidadeRequest {
        private Integer quantidade;

        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    }
}