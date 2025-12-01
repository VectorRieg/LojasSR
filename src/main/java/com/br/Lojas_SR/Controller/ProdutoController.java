package com.br.Lojas_SR.Controller;

import com.br.Lojas_SR.Entity.Produto;
import com.br.Lojas_SR.Service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // Listar todos os produtos
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    // Buscar produto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    // Listar produtos em destaque
    @GetMapping("/destaques")
    public ResponseEntity<List<Produto>> listarDestaques() {
        List<Produto> produtos = produtoService.listarDestaques();
        return ResponseEntity.ok(produtos);
    }

    // Buscar por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Produto>> buscarPorCategoria(@PathVariable String categoria) {
        List<Produto> produtos = produtoService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(produtos);
    }

    // Buscar por nome
    @GetMapping("/buscar")
    public ResponseEntity<List<Produto>> buscarPorNome(@RequestParam String nome) {
        List<Produto> produtos = produtoService.buscarPorNome(nome);
        return ResponseEntity.ok(produtos);
    }

    // Listar produtos em promoção
    @GetMapping("/promocoes")
    public ResponseEntity<List<Produto>> listarPromocoes() {
        List<Produto> produtos = produtoService.listarPromocoes();
        return ResponseEntity.ok(produtos);
    }

    // Listar produtos disponíveis (ativos e com estoque)
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Produto>> listarDisponiveis() {
        List<Produto> produtos = produtoService.listarDisponiveis();
        return ResponseEntity.ok(produtos);
    }

    // Buscar por marca
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Produto>> buscarPorMarca(@PathVariable String marca) {
        List<Produto> produtos = produtoService.buscarPorMarca(marca);
        return ResponseEntity.ok(produtos);
    }

    // Criar produto (admin)
    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        Produto novoProduto = produtoService.criar(produto);
        return ResponseEntity.ok(novoProduto);
    }

    // Atualizar produto (admin)
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        Produto produtoAtualizado = produtoService.atualizar(id, produto);
        return ResponseEntity.ok(produtoAtualizado);
    }

    // Deletar produto (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
