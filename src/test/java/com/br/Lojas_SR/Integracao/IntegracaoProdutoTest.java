package com.br.Lojas_SR.Integracao;

import com.br.Lojas_SR.Entity.Produto;
import com.br.Lojas_SR.Service.ProdutoService;
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
@DisplayName("Testes de Integração - Produto")
class IntegracaoProdutoTest {

    @Autowired
    private ProdutoService produtoService;

    @Test
    @DisplayName("Deve criar e buscar produto com todos os dados")
    void deveCriarEBuscarProdutoComTodosDados() {
        Produto produto = new Produto();
        produto.setNome("Notebook Dell");
        produto.setDescricao("Notebook Dell G15 Gaming");
        produto.setPreco(new BigDecimal("5000.00"));
        produto.setEstoque(10);
        produto.setCategoria("Eletrônicos");
        produto.setMarca("Dell");
        produto.setCodigo("NOT-DELL-001");

        Produto criado = produtoService.criar(produto);

        assertNotNull(criado.getId());
        assertEquals("Notebook Dell", criado.getNome());
        assertEquals(new BigDecimal("5000.00"), criado.getPreco());

        Produto buscado = produtoService.buscarPorId(criado.getId());
        assertEquals(criado.getId(), buscado.getId());
    }

    @Test
    @DisplayName("Deve listar produtos por diferentes filtros")
    void deveListarProdutosPorDiferentesFiltros() {
        // Criar produtos variados
        Produto notebook = criarProdutoTeste("Notebook", "Eletrônicos", "Dell", new BigDecimal("5000.00"), true, true);
        Produto mouse = criarProdutoTeste("Mouse", "Periféricos", "Logitech", new BigDecimal("200.00"), true, false);
        Produto teclado = criarProdutoTeste("Teclado", "Periféricos", "Razer", new BigDecimal("500.00"), false, false);

        produtoService.criar(notebook);
        produtoService.criar(mouse);
        produtoService.criar(teclado);

        // Teste 1: Buscar ativos
        List<Produto> ativos = produtoService.listarAtivos();
        assertTrue(ativos.size() >= 2);

        // Teste 2: Buscar por categoria
        List<Produto> perifericos = produtoService.buscarPorCategoria("Periféricos");
        assertTrue(perifericos.size() >= 2);

        // Teste 3: Buscar em destaque
        List<Produto> destaques = produtoService.listarDestaques();
        assertTrue(destaques.size() >= 1);

        // Teste 4: Buscar por faixa de preço
        List<Produto> precoBaixo = produtoService.buscarPorFaixaPreco(
                new BigDecimal("100.00"),
                new BigDecimal("300.00")
        );
        assertTrue(precoBaixo.size() >= 1);
    }

    @Test
    @DisplayName("Deve gerenciar estoque corretamente")
    void deveGerenciarEstoqueCorretamente() {
        Produto produto = criarProdutoTeste("Produto Estoque", "Teste", "Teste", new BigDecimal("100.00"), true, false);
        produto.setEstoque(10);
        Produto criado = produtoService.criar(produto);

        // Reduzir estoque
        produtoService.reduzirEstoque(criado.getId(), 3);
        Produto atualizado = produtoService.buscarPorId(criado.getId());
        assertEquals(7, atualizado.getEstoque());

        // Tentar reduzir mais que o disponível
        assertThrows(RuntimeException.class, () -> {
            produtoService.reduzirEstoque(criado.getId(), 10);
        });
    }

    @Test
    @DisplayName("Deve atualizar preço e manter histórico")
    void deveAtualizarPrecoEManterHistorico() {
        Produto produto = criarProdutoTeste("Produto Preço", "Teste", "Teste", new BigDecimal("1000.00"), true, false);
        Produto criado = produtoService.criar(produto);

        BigDecimal precoOriginal = criado.getPreco();

        // Atualizar preço
        Produto atualizacao = new Produto();
        atualizacao.setPreco(new BigDecimal("1200.00"));

        Produto atualizado = produtoService.atualizar(criado.getId(), atualizacao);

        assertEquals(new BigDecimal("1200.00"), atualizado.getPreco());
        assertNotEquals(precoOriginal, atualizado.getPreco());
    }

    @Test
    @DisplayName("Deve buscar produtos disponíveis (ativos com estoque)")
    void deveBuscarProdutosDisponiveisAtivosComEstoque() {
        Produto comEstoque = criarProdutoTeste("Com Estoque", "Teste", "Teste", new BigDecimal("100.00"), true, false);
        comEstoque.setEstoque(10);

        Produto semEstoque = criarProdutoTeste("Sem Estoque", "Teste", "Teste", new BigDecimal("100.00"), true, false);
        semEstoque.setEstoque(0);

        Produto inativo = criarProdutoTeste("Inativo", "Teste", "Teste", new BigDecimal("100.00"), false, false);
        inativo.setEstoque(10);

        produtoService.criar(comEstoque);
        produtoService.criar(semEstoque);
        produtoService.criar(inativo);

        List<Produto> disponiveis = produtoService.listarDisponiveis();

        assertTrue(disponiveis.stream().anyMatch(p -> p.getNome().equals("Com Estoque")));
        assertFalse(disponiveis.stream().anyMatch(p -> p.getNome().equals("Sem Estoque")));
        assertFalse(disponiveis.stream().anyMatch(p -> p.getNome().equals("Inativo")));
    }

    @Test
    @DisplayName("Deve buscar produtos com filtros combinados")
    void deveBuscarProdutosComFiltrosCombinados() {
        Produto p1 = criarProdutoTeste("Dell Monitor", "Eletrônicos", "Dell", new BigDecimal("2000.00"), true, false);
        Produto p2 = criarProdutoTeste("Dell Notebook", "Eletrônicos", "Dell", new BigDecimal("5000.00"), true, false);
        Produto p3 = criarProdutoTeste("LG Monitor", "Eletrônicos", "LG", new BigDecimal("1500.00"), true, false);

        produtoService.criar(p1);
        produtoService.criar(p2);
        produtoService.criar(p3);

        // Buscar: Eletrônicos + Dell + Entre 1000 e 3000
        List<Produto> filtrados = produtoService.buscarComFiltros(
                "Eletrônicos",
                "Dell",
                new BigDecimal("1000.00"),
                new BigDecimal("3000.00")
        );

        assertEquals(1, filtrados.stream().filter(p -> p.getNome().equals("Dell Monitor")).count());
        assertEquals(0, filtrados.stream().filter(p -> p.getNome().equals("Dell Notebook")).count());
        assertEquals(0, filtrados.stream().filter(p -> p.getNome().equals("LG Monitor")).count());
    }

    private Produto criarProdutoTeste(String nome, String categoria, String marca, BigDecimal preco, boolean ativo, boolean destaque) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setCategoria(categoria);
        produto.setMarca(marca);
        produto.setPreco(preco);
        produto.setEstoque(10);
        return produto;
    }
}