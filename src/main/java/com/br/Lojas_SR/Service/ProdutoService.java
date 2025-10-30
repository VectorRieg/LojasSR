package com.br.Lojas_SR.Service;

import com.br.Lojas_SR.Entity.Produto;
import com.br.Lojas_SR.Repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // Criar produto
    public Produto criar(Produto produto) {
        // Validações básicas
        if (produto.getNome() == null || produto.getNome().isEmpty()) {
            throw new RuntimeException("Nome do produto é obrigatório");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Preço inválido");
        }

        return produtoRepository.save(produto);
    }

    // Buscar produto por ID
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    // Listar todos os produtos
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    // Listar produtos ativos
    public List<Produto> listarAtivos() {
        return produtoRepository.findByAtivo(true);
    }

    // Listar produtos disponíveis (ativos e com estoque)
    public List<Produto> listarDisponiveis() {
        return produtoRepository.findProdutosDisponiveis();
    }

    // Listar produtos em destaque
    public List<Produto> listarDestaques() {
        return produtoRepository.findByDestaque(true);
    }

    // Listar produtos em promoção
    public List<Produto> listarPromocoes() {
        return produtoRepository.findByEmPromocao(true);
    }

    // Buscar por categoria
    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    // Buscar por marca
    public List<Produto> buscarPorMarca(String marca) {
        return produtoRepository.findByMarca(marca);
    }

    // Buscar por nome
    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Buscar por faixa de preço
    public List<Produto> buscarPorFaixaPreco(BigDecimal precoMin, BigDecimal precoMax) {
        return produtoRepository.findByPrecoBetween(precoMin, precoMax);
    }

    // Buscar com filtros múltiplos
    public List<Produto> buscarComFiltros(String categoria, String marca,
                                          BigDecimal precoMin, BigDecimal precoMax) {
        return produtoRepository.buscarComFiltros(categoria, marca, precoMin, precoMax);
    }

    // Atualizar produto
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto produto = buscarPorId(id);

        // Atualizar campos
        if (produtoAtualizado.getNome() != null) {
            produto.setNome(produtoAtualizado.getNome());
        }
        if (produtoAtualizado.getDescricao() != null) {
            produto.setDescricao(produtoAtualizado.getDescricao());
        }
        if (produtoAtualizado.getPreco() != null) {
            produto.setPreco(produtoAtualizado.getPreco());
        }
        if (produtoAtualizado.getEstoque() != null) {
            produto.setEstoque(produtoAtualizado.getEstoque());
        }
        if (produtoAtualizado.getCategoria() != null) {
            produto.setCategoria(produtoAtualizado.getCategoria());
        }
        if (produtoAtualizado.getMarca() != null) {
            produto.setMarca(produtoAtualizado.getMarca());
        }

        return produtoRepository.save(produto);
    }

    // Atualizar estoque
    public Produto atualizarEstoque(Long id, Integer quantidade) {
        Produto produto = buscarPorId(id);
        produto.setEstoque(quantidade);
        return produtoRepository.save(produto);
    }

    // Reduzir estoque (ao finalizar compra)
    public void reduzirEstoque(Long id, Integer quantidade) {
        Produto produto = buscarPorId(id);

        if (produto.getEstoque() < quantidade) {
            throw new RuntimeException("Estoque insuficiente");
        }

        produto.setEstoque(produto.getEstoque() - quantidade);
        produtoRepository.save(produto);
    }

    // Deletar produto
    public void deletar(Long id) {
        Produto produto = buscarPorId(id);
        produtoRepository.save(produto);
    }

    // Deletar permanentemente
    public void deletarPermanente(Long id) {
        produtoRepository.deleteById(id);
    }
}