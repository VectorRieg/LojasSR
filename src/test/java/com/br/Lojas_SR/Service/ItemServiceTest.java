package com.br.Lojas_SR.Service;

import com.br.Lojas_SR.Entity.Carrinho;
import com.br.Lojas_SR.Entity.Item;
import com.br.Lojas_SR.Entity.Produto;
import com.br.Lojas_SR.Repository.CarrinhoRepository;
import com.br.Lojas_SR.Repository.ItemRepository;
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
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CarrinhoRepository carrinhoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CarrinhoService carrinhoService;

    @InjectMocks
    private ItemService itemService;

    private Item item;
    private Carrinho carrinho;
    private Produto produto;

    @BeforeEach
    void setUp() {
        carrinho = new Carrinho();
        carrinho.setId(1L);

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Notebook");
        produto.setPreco(new BigDecimal("3000.00"));
        produto.setEstoque(10);

        item = new Item();
        item.setId(1L);
        item.setCarrinho(carrinho);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("3000.00"));
    }

    @Test
    void deveAdicionarItemComSucesso() {
        when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(carrinho));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(itemRepository.existsByCarrinhoIdAndProdutoId(1L, 1L)).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item resultado = itemService.adicionar(1L, 1L, 2);

        assertNotNull(resultado);
        assertEquals(2, resultado.getQuantidade());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void deveLancarExcecaoQuandoProdutoInativo() {
        when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(carrinho));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            itemService.adicionar(1L, 1L, 2);
        });

        assertEquals("Produto indisponível", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(carrinho));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            itemService.adicionar(1L, 1L, 20);
        });

        assertEquals("Estoque insuficiente", exception.getMessage());
    }

    @Test
    void deveListarItensPorCarrinho() {
        List<Item> itens = Arrays.asList(item);
        when(carrinhoRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findByCarrinhoId(1L)).thenReturn(itens);

        List<Item> resultado = itemService.listarPorCarrinho(1L);

        assertEquals(1, resultado.size());
        verify(itemRepository, times(1)).findByCarrinhoId(1L);
    }

    @Test
    void deveAtualizarQuantidade() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item resultado = itemService.atualizarQuantidade(1L, 5);

        assertNotNull(resultado);
        assertEquals(5, resultado.getQuantidade());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeInvalida() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            itemService.atualizarQuantidade(1L, 0);
        });

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    void deveRemoverItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).deleteById(1L);

        itemService.remover(1L);

        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveContarItens() {
        when(itemRepository.countByCarrinhoId(1L)).thenReturn(3L);

        Long total = itemService.contarItens(1L);

        assertEquals(3L, total);
        verify(itemRepository, times(1)).countByCarrinhoId(1L);
    }
}