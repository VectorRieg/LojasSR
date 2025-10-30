package com.br.Lojas_SR.Repository;

import com.br.Lojas_SR.Entity.Carrinho;
import com.br.Lojas_SR.Entity.Item;
import com.br.Lojas_SR.Entity.Produto;
import com.br.Lojas_SR.Entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private Carrinho carrinho;
    private Produto produto;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senha");
        entityManager.persist(usuario);

        carrinho = new Carrinho();
        carrinho.setUsuario(usuario);
        carrinho.setItens(new ArrayList<>());
        entityManager.persist(carrinho);

        produto = new Produto();
        produto.setNome("Notebook");
        produto.setPreco(new BigDecimal("3000.00"));
        produto.setEstoque(10);
        entityManager.persist(produto);

        item = new Item();
        item.setCarrinho(carrinho);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("3000.00"));
    }

    @Test
    void deveSalvarItem() {
        Item salvo = itemRepository.save(item);

        assertNotNull(salvo.getId());
        assertEquals(2, salvo.getQuantidade());
    }

    @Test
    void deveListarItensPorCarrinho() {
        entityManager.persist(item);
        entityManager.flush();

        List<Item> itens = itemRepository.findByCarrinhoId(carrinho.getId());

        assertEquals(1, itens.size());
        assertEquals(produto.getId(), itens.get(0).getProduto().getId());
    }

    @Test
    void deveBuscarItemPorCarrinhoEProduto() {
        entityManager.persist(item);
        entityManager.flush();

        Optional<Item> encontrado = itemRepository.findByCarrinhoIdAndProdutoId(
                carrinho.getId(), produto.getId()
        );

        assertTrue(encontrado.isPresent());
        assertEquals(2, encontrado.get().getQuantidade());
    }

    @Test
    void deveContarItensDoCarrinho() {
        entityManager.persist(item);
        entityManager.flush();

        Long total = itemRepository.countByCarrinhoId(carrinho.getId());

        assertEquals(1L, total);
    }

    @Test
    void deveVerificarSeProdutoEstaNoCarrinho() {
        entityManager.persist(item);
        entityManager.flush();

        boolean existe = itemRepository.existsByCarrinhoIdAndProdutoId(
                carrinho.getId(), produto.getId()
        );

        assertTrue(existe);
    }
}
