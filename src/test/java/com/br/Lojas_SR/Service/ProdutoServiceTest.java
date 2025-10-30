package com.br.Lojas_SR.Service;

import com.br.Lojas_SR.Entity.Produto;
import com.br.Lojas_SR.Repository.ProdutoRepository;
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
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Notebook");
        produto.setDescricao("Notebook Gamer");
        produto.setPreco(new BigDecimal("3000.00"));
        produto.setEstoque(10);
        produto.setCategoria("Eletrônicos");
        produto.setMarca("Dell");
    }

    @Test
    void deveCriarProdutoComSucesso() {
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        Produto resultado = produtoService.criar(produto);

        assertNotNull(resultado);
        assertEquals("Notebook", resultado.getNome());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void deveLancarExcecaoQuandoNomeVazio() {
        produto.setNome("");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            produtoService.criar(produto);
        });

        assertEquals("Nome do produto é obrigatório", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoPrecoInvalido() {
        produto.setPreco(BigDecimal.ZERO);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            produtoService.criar(produto);
        });

        assertEquals("Preço inválido", exception.getMessage());
    }

    @Test
    void deveBuscarProdutoPorId() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        Produto resultado = produtoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    void deveListarTodosProdutos() {
        List<Produto> produtos = Arrays.asList(produto);
        when(produtoRepository.findAll()).thenReturn(produtos);

        List<Produto> resultado = produtoService.listarTodos();

        assertEquals(1, resultado.size());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void deveListarProdutosAtivos() {
        List<Produto> produtos = Arrays.asList(produto);
        when(produtoRepository.findByAtivo(true)).thenReturn(produtos);

        List<Produto> resultado = produtoService.listarAtivos();

        assertEquals(1, resultado.size());
        verify(produtoRepository, times(1)).findByAtivo(true);
    }

    @Test
    void deveBuscarPorCategoria() {
        List<Produto> produtos = Arrays.asList(produto);
        when(produtoRepository.findByCategoria("Eletrônicos")).thenReturn(produtos);

        List<Produto> resultado = produtoService.buscarPorCategoria("Eletrônicos");

        assertEquals(1, resultado.size());
        verify(produtoRepository, times(1)).findByCategoria("Eletrônicos");
    }

    @Test
    void deveAtualizarProduto() {
        Produto produtoAtualizado = new Produto();
        produtoAtualizado.setNome("Notebook Atualizado");
        produtoAtualizado.setPreco(new BigDecimal("3500.00"));

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        Produto resultado = produtoService.atualizar(1L, produtoAtualizado);

        assertNotNull(resultado);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void deveReduzirEstoque() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        produtoService.reduzirEstoque(1L, 3);

        assertEquals(7, produto.getEstoque());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            produtoService.reduzirEstoque(1L, 20);
        });

        assertEquals("Estoque insuficiente", exception.getMessage());
    }

    @Test
    void deveDeletarProduto() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        produtoService.deletar(1L);

        verify(produtoRepository, times(1)).save(produto);
    }
}