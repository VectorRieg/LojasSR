package com.br.Lojas_SR.Integracao;

import com.br.Lojas_SR.Entity.*;
import com.br.Lojas_SR.Service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Testes de Integração - Item de Carrinho")
class IntegracaoItemTest {

    @Autowired
    private AcessoService acessoService;

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ItemService itemService;

    private Usuario usuario;
    private Carrinho carrinho;
    private Produto produto1;
    private Produto produto2;
    private Produto produto3;

    @BeforeEach
    void setUp() {
        // Criar usuário
        usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("item" + System.currentTimeMillis() + "@email.com");
        usuario.setSenha("senha123");
        usuario = acessoService.registrar(usuario);

        // Criar carrinho
        carrinho = carrinhoService.criar(usuario.getId());

        // Criar produtos
        produto1 = new Produto();
        produto1.setNome("Notebook");
        produto1.setPreco(new BigDecimal("3000.00"));
        produto1.setEstoque(10);
        produto1 = produtoService.criar(produto1);

        produto2 = new Produto();
        produto2.setNome("Mouse");
        produto2.setPreco(new BigDecimal("100.00"));
        produto2.setEstoque(20);
        produto2 = produtoService.criar(produto2);

        produto3 = new Produto();
        produto3.setNome("Teclado");
        produto3.setPreco(new BigDecimal("500.00"));
        produto3.setEstoque(2);
        produto3 = produtoService.criar(produto3);
    }

    @Test
    @DisplayName("Deve adicionar item ao carrinho com sucesso")
    void deveAdicionarItemAoCarrinhoComSucesso() {
        Item item = itemService.adicionar(carrinho.getId(), produto1.getId(), 2);

        assertNotNull(item.getId());
        assertEquals(2, item.getQuantidade());
        assertEquals(carrinho.getId(), item.getCarrinho().getId());
        assertEquals(produto1.getId(), item.getProduto().getId());
    }

    @Test
    @DisplayName("Deve usar preço promocional quando disponível")
    void deveUsarPrecoPromocionalQuandoDisponivel() {
        Item item = itemService.adicionar(carrinho.getId(), produto1.getId(), 1);

        // Produto1 tem preço promocional de 2700.00
        assertEquals(new BigDecimal("2700.00"), item.getPrecoUnitario());
        assertNotEquals(produto1.getPreco(), item.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve usar preço normal quando não tem promoção")
    void deveUsarPrecoNormalQuandoNaoTemPromocao() {
        Item item = itemService.adicionar(carrinho.getId(), produto2.getId(), 1);

        // Produto2 não tem preço promocional
        assertEquals(new BigDecimal("100.00"), item.getPrecoUnitario());
        assertEquals(produto2.getPreco(), item.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve incrementar quantidade ao adicionar produto duplicado")
    void deveIncrementarQuantidadeAoAdicionarProdutoDuplicado() {
        // Adicionar pela primeira vez
        Item item1 = itemService.adicionar(carrinho.getId(), produto1.getId(), 2);
        assertEquals(2, item1.getQuantidade());

        // Adicionar novamente o mesmo produto
        Item item2 = itemService.adicionar(carrinho.getId(), produto1.getId(), 3);
        assertEquals(5, item2.getQuantidade()); // 2 + 3 = 5

        // Verificar que ainda é apenas 1 item no carrinho
        Long count = itemService.contarItens(carrinho.getId());
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Deve listar todos os itens do carrinho")
    void deveListarTodosOsItensDoCarrinho() {
        itemService.adicionar(carrinho.getId(), produto1.getId(), 2);
        itemService.adicionar(carrinho.getId(), produto2.getId(), 3);
        itemService.adicionar(carrinho.getId(), produto3.getId(), 1);

        List<Item> itens = itemService.listarPorCarrinho(carrinho.getId());

        assertEquals(3, itens.size());
        assertTrue(itens.stream().anyMatch(i -> i.getProduto().getNome().equals("Notebook")));
        assertTrue(itens.stream().anyMatch(i -> i.getProduto().getNome().equals("Mouse")));
        assertTrue(itens.stream().anyMatch(i -> i.getProduto().getNome().equals("Teclado")));
    }

    @Test
    @DisplayName("Deve atualizar quantidade do item")
    void deveAtualizarQuantidadeDoItem() {
        Item item = itemService.adicionar(carrinho.getId(), produto1.getId(), 2);
        assertEquals(2, item.getQuantidade());

        Item itemAtualizado = itemService.atualizarQuantidade(item.getId(), 5);
        assertEquals(5, itemAtualizado.getQuantidade());
    }

    @Test
    @DisplayName("Não deve permitir quantidade zero ou negativa")
    void naoDevePermitirQuantidadeZeroOuNegativa() {
        Item item = itemService.adicionar(carrinho.getId(), produto1.getId(), 2);

        assertThrows(RuntimeException.class, () -> {
            itemService.atualizarQuantidade(item.getId(), 0);
        });

        assertThrows(RuntimeException.class, () -> {
            itemService.atualizarQuantidade(item.getId(), -1);
        });
    }

    @Test
    @DisplayName("Não deve adicionar quantidade maior que estoque")
    void naoDeveAdicionarQuantidadeMaiorQueEstoque() {
        // Produto3 tem apenas 2 unidades em estoque
        assertThrows(RuntimeException.class, () -> {
            itemService.adicionar(carrinho.getId(), produto3.getId(), 5);
        });
    }

    @Test
    @DisplayName("Não deve atualizar para quantidade maior que estoque")
    void naoDeveAtualizarParaQuantidadeMaiorQueEstoque() {
        Item item = itemService.adicionar(carrinho.getId(), produto3.getId(), 1);

        assertThrows(RuntimeException.class, () -> {
            itemService.atualizarQuantidade(item.getId(), 10);
        });
    }

    @Test
    @DisplayName("Deve calcular subtotal corretamente")
    void deveCalcularSubtotalCorretamente() {
        Item item = itemService.adicionar(carrinho.getId(), produto1.getId(), 3);

        BigDecimal subtotal = itemService.calcularSubtotal(item.getId());

        // 3 * 2700.00 = 8100.00
        assertEquals(new BigDecimal("8100.00"), subtotal);
    }

    @Test
    @DisplayName("Deve remover item do carrinho")
    void deveRemoverItemDoCarrinho() {
        Item item1 = itemService.adicionar(carrinho.getId(), produto1.getId(), 2);
        Item item2 = itemService.adicionar(carrinho.getId(), produto2.getId(), 3);

        Long countAntes = itemService.contarItens(carrinho.getId());
        assertEquals(2L, countAntes);

        itemService.remover(item1.getId());

        Long countDepois = itemService.contarItens(carrinho.getId());
        assertEquals(1L, countDepois);

        List<Item> itensRestantes = itemService.listarPorCarrinho(carrinho.getId());
        assertEquals(1, itensRestantes.size());
        assertEquals(produto2.getId(), itensRestantes.get(0).getProduto().getId());
    }

    @Test
    @DisplayName("Não deve adicionar produto inativo ao carrinho")
    void naoDeveAdicionarProdutoInativoAoCarrinho() {
        // Desativar produto
        produtoService.deletar(produto1.getId());

        assertThrows(RuntimeException.class, () -> {
            itemService.adicionar(carrinho.getId(), produto1.getId(), 1);
        });
    }

    @Test
    @DisplayName("Deve contar corretamente o número de itens")
    void deveContarCorretamenteONumeroDeItens() {
        assertEquals(0L, itemService.contarItens(carrinho.getId()));

        itemService.adicionar(carrinho.getId(), produto1.getId(), 2);
        assertEquals(1L, itemService.contarItens(carrinho.getId()));

        itemService.adicionar(carrinho.getId(), produto2.getId(), 3);
        assertEquals(2L, itemService.contarItens(carrinho.getId()));

        itemService.adicionar(carrinho.getId(), produto3.getId(), 1);
        assertEquals(3L, itemService.contarItens(carrinho.getId()));
    }

    @Test
    @DisplayName("Deve preservar preço do produto no momento da adição")
    void devePreservarPrecoDoProdutoNoMomentoDaAdicao() {
        // Adicionar produto ao carrinho
        Item item = itemService.adicionar(carrinho.getId(), produto2.getId(), 1);
        BigDecimal precoOriginal = item.getPrecoUnitario();
        assertEquals(new BigDecimal("100.00"), precoOriginal);

        // Alterar preço do produto
        Produto produtoAtualizado = new Produto();
        produtoAtualizado.setPreco(new BigDecimal("150.00"));
        produtoService.atualizar(produto2.getId(), produtoAtualizado);

        // Verificar que o item mantém o preço original
        Item itemBuscado = itemService.buscarPorId(item.getId());
        assertEquals(precoOriginal, itemBuscado.getPrecoUnitario());
        assertEquals(new BigDecimal("100.00"), itemBuscado.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve adicionar múltiplos itens e calcular total corretamente")
    void deveAdicionarMultiplosItensECalcularTotalCorretamente() {
        // Adicionar 3 produtos diferentes
        itemService.adicionar(carrinho.getId(), produto1.getId(), 2); // 2 * 2700 = 5400
        itemService.adicionar(carrinho.getId(), produto2.getId(), 3); // 3 * 100 = 300
        itemService.adicionar(carrinho.getId(), produto3.getId(), 1); // 1 * 500 = 500

        BigDecimal total = carrinhoService.calcularTotal(carrinho.getId());
        assertEquals(new BigDecimal("6200.00"), total); // 5400 + 300 + 500
    }

    @Test
    @DisplayName("Deve buscar item por ID")
    void deveBuscarItemPorId() {
        Item item = itemService.adicionar(carrinho.getId(), produto1.getId(), 2);
        Long itemId = item.getId();

        Item itemBuscado = itemService.buscarPorId(itemId);

        assertNotNull(itemBuscado);
        assertEquals(itemId, itemBuscado.getId());
        assertEquals(2, itemBuscado.getQuantidade());
        assertEquals(produto1.getId(), itemBuscado.getProduto().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar item inexistente")
    void deveLancarExcecaoAoBuscarItemInexistente() {
        assertThrows(RuntimeException.class, () -> {
            itemService.buscarPorId(9999L);
        });
    }
}