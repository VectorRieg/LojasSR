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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Testes de Integração - Carrinho Completo")
class IntegracaoCarrinhoTest {

    @Autowired
    private AcessoService acessoService;

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ItemService itemService;

    private Usuario usuario;
    private Produto produto1;
    private Produto produto2;

    @BeforeEach
    void setUp() {
        // Criar usuário
        usuario = new Usuario();
        usuario.setNome("João Carrinho");
        usuario.setEmail("carrinho" + System.currentTimeMillis() + "@email.com");
        usuario.setSenha("senha123");
        usuario = acessoService.registrar(usuario);

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
    }

    @Test
    @DisplayName("Deve realizar fluxo completo de compra")
    void deveRealizarFluxoCompletoDeCompra() {
        // 1. Criar carrinho
        Carrinho carrinho = carrinhoService.criar(usuario.getId());
        assertNotNull(carrinho);
        assertNotNull(carrinho.getId());

        // 2. Adicionar primeiro item
        Item item1 = itemService.adicionar(carrinho.getId(), produto1.getId(), 2);
        assertNotNull(item1);
        assertEquals(2, item1.getQuantidade());
        assertEquals(new BigDecimal("3000.00"), item1.getPrecoUnitario());

        // 3. Adicionar segundo item
        Item item2 = itemService.adicionar(carrinho.getId(), produto2.getId(), 3);
        assertNotNull(item2);
        assertEquals(3, item2.getQuantidade());

        // 4. Verificar total do carrinho (2*3000 + 3*100 = 6300)
        BigDecimal total = carrinhoService.calcularTotal(carrinho.getId());
        assertEquals(new BigDecimal("6300.00"), total);

        // 5. Atualizar quantidade do primeiro item
        item1 = itemService.atualizarQuantidade(item1.getId(), 3);
        assertEquals(3, item1.getQuantidade());

        // 6. Verificar novo total (3*3000 + 3*100 = 9300)
        total = carrinhoService.calcularTotal(carrinho.getId());
        assertEquals(new BigDecimal("9300.00"), total);

        // 7. Remover segundo item
        itemService.remover(item2.getId());
        Long count = itemService.contarItens(carrinho.getId());
        assertEquals(1L, count);

        // 8. Verificar total final (3*3000 = 9000)
        total = carrinhoService.calcularTotal(carrinho.getId());
        assertEquals(new BigDecimal("9000.00"), total);

        // 9. Limpar carrinho
        carrinhoService.limpar(carrinho.getId());
        count = itemService.contarItens(carrinho.getId());
        assertEquals(0L, count);
    }

    @Test
    @DisplayName("Não deve adicionar produto sem estoque")
    void naoDeveAdicionarProdutoSemEstoque() {
        produto1.setEstoque(1);
        produtoService.atualizar(produto1.getId(), produto1);

        Carrinho carrinho = carrinhoService.criar(usuario.getId());

        assertThrows(RuntimeException.class, () -> {
            itemService.adicionar(carrinho.getId(), produto1.getId(), 5);
        });
    }

    @Test
    @DisplayName("Não deve adicionar produto inativo")
    void naoDeveAdicionarProdutoInativo() {
        produtoService.deletar(produto1.getId());

        Carrinho carrinho = carrinhoService.criar(usuario.getId());

        assertThrows(RuntimeException.class, () -> {
            itemService.adicionar(carrinho.getId(), produto1.getId(), 1);
        });
    }

    @Test
    @DisplayName("Deve impedir criação de dois carrinhos para mesmo usuário")
    void deveImpedirCriacaoDeDoisCarrinhosParaMesmoUsuario() {
        carrinhoService.criar(usuario.getId());

        assertThrows(RuntimeException.class, () -> {
            carrinhoService.criar(usuario.getId());
        });
    }

    @Test
    @DisplayName("Deve adicionar mesmo produto duas vezes (aumentar quantidade)")
    void deveAdicionarMesmoProdutoDuasVezesAumentarQuantidade() {
        Carrinho carrinho = carrinhoService.criar(usuario.getId());

        // Adicionar produto pela primeira vez
        Item item1 = itemService.adicionar(carrinho.getId(), produto1.getId(), 2);
        assertEquals(2, item1.getQuantidade());

        // Adicionar mesmo produto novamente
        Item item2 = itemService.adicionar(carrinho.getId(), produto1.getId(), 3);
        assertEquals(5, item2.getQuantidade()); // 2 + 3

        // Verificar que ainda é apenas 1 item no carrinho
        Long count = itemService.contarItens(carrinho.getId());
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Deve calcular subtotal corretamente para cada item")
    void deveCalcularSubtotalCorretamenteParaCadaItem() {
        Carrinho carrinho = carrinhoService.criar(usuario.getId());

        Item item1 = itemService.adicionar(carrinho.getId(), produto1.getId(), 2);
        Item item2 = itemService.adicionar(carrinho.getId(), produto2.getId(), 5);

        BigDecimal subtotal1 = itemService.calcularSubtotal(item1.getId());
        BigDecimal subtotal2 = itemService.calcularSubtotal(item2.getId());

        assertEquals(new BigDecimal("6000.00"), subtotal1); // 2 * 3000
        assertEquals(new BigDecimal("500.00"), subtotal2);  // 5 * 100
    }

    @Test
    @DisplayName("Deve preservar preço do produto no momento da adição")
    void devePreservarPrecoDoProdutoNoMomentoDaAdicao() {
        Carrinho carrinho = carrinhoService.criar(usuario.getId());

        // Adicionar produto ao carrinho
        Item item = itemService.adicionar(carrinho.getId(), produto1.getId(), 1);
        BigDecimal precoOriginal = item.getPrecoUnitario();

        // Alterar preço do produto
        Produto produtoAtualizado = new Produto();
        produtoAtualizado.setPreco(new BigDecimal("5000.00"));
        produtoService.atualizar(produto1.getId(), produtoAtualizado);

        // Verificar que o item mantém o preço original
        Item itemBuscado = itemService.buscarPorId(item.getId());
        assertEquals(precoOriginal, itemBuscado.getPrecoUnitario());
        assertEquals(new BigDecimal("3000.00"), itemBuscado.getPrecoUnitario());
    }
}