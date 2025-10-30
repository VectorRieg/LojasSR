package com.br.Lojas_SR.Repository;

import com.br.Lojas_SR.Entity.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProdutoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProdutoRepository produtoRepository;

    private Produto notebook;
    private Produto mouse;
    private Produto teclado;
    private Produto monitor;

    @BeforeEach
    void setUp() {
        // Produto 1: Notebook (ativo, destaque, eletrônicos, Dell)
        notebook = new Produto();
        notebook.setNome("Notebook Gamer");
        notebook.setDescricao("Notebook Dell G15 com RTX 3060");
        notebook.setPreco(new BigDecimal("5000.00"));
        notebook.setEstoque(15);
        notebook.setCategoria("Eletrônicos");
        notebook.setMarca("Dell");
        notebook.setCodigo("NOT-DELL-001");

        // Produto 2: Mouse (ativo, sem destaque, periféricos, Logitech)
        mouse = new Produto();
        mouse.setNome("Mouse Gamer RGB");
        mouse.setDescricao("Mouse Logitech G502");
        mouse.setPreco(new BigDecimal("250.00"));
        mouse.setEstoque(50);
        mouse.setCategoria("Periféricos");
        mouse.setMarca("Logitech");

        // Produto 3: Teclado (inativo, periféricos, Razer)
        teclado = new Produto();
        teclado.setNome("Teclado Mecânico");
        teclado.setDescricao("Teclado Razer BlackWidow");
        teclado.setPreco(new BigDecimal("800.00"));
        teclado.setEstoque(0);
        teclado.setCategoria("Periféricos");
        teclado.setMarca("Razer");

        // Produto 4: Monitor (ativo, destaque, eletrônicos, LG)
        monitor = new Produto();
        monitor.setNome("Monitor UltraWide");
        monitor.setDescricao("Monitor LG 34 polegadas");
        monitor.setPreco(new BigDecimal("2000.00"));
        monitor.setEstoque(10);
        monitor.setCategoria("Eletrônicos");
        monitor.setMarca("LG");
        monitor.setCodigo("MON-LG-001");
    }

    @Test
    @DisplayName("Deve salvar produto com sucesso")
    void deveSalvarProdutoComSucesso() {
        Produto salvo = produtoRepository.save(notebook);

        assertNotNull(salvo.getId());
        assertEquals("Notebook Gamer", salvo.getNome());
        assertEquals("Dell", salvo.getMarca());
        assertEquals(new BigDecimal("5000.00"), salvo.getPreco());
    }

    @Test
    @DisplayName("Deve buscar produto por ID")
    void deveBuscarProdutoPorId() {
        entityManager.persist(notebook);
        entityManager.flush();

        Optional<Produto> encontrado = produtoRepository.findById(notebook.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("Notebook Gamer", encontrado.get().getNome());
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    void deveListarTodosProdutos() {
        entityManager.persist(notebook);
        entityManager.persist(mouse);
        entityManager.persist(teclado);
        entityManager.flush();

        List<Produto> produtos = produtoRepository.findAll();

        assertEquals(3, produtos.size());
    }

    @Test
    @DisplayName("Deve listar produtos inativos")
    void deveListarProdutosInativos() {
        entityManager.persist(notebook);
        entityManager.persist(teclado);
        entityManager.flush();

        List<Produto> inativos = produtoRepository.findByAtivo(false);

        assertEquals(1, inativos.size());
        assertEquals("Teclado Mecânico", inativos.get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar produtos por categoria")
    void deveBuscarProdutosPorCategoria() {
        entityManager.persist(notebook);
        entityManager.persist(mouse);
        entityManager.persist(monitor);
        entityManager.flush();

        List<Produto> eletronicos = produtoRepository.findByCategoria("Eletrônicos");
        List<Produto> perifericos = produtoRepository.findByCategoria("Periféricos");

        assertEquals(2, eletronicos.size());
        assertEquals(1, perifericos.size());
    }

    @Test
    @DisplayName("Deve buscar produtos por marca")
    void deveBuscarProdutosPorMarca() {
        entityManager.persist(notebook);
        entityManager.persist(mouse);
        entityManager.persist(teclado);
        entityManager.flush();

        List<Produto> dell = produtoRepository.findByMarca("Dell");
        List<Produto> logitech = produtoRepository.findByMarca("Logitech");

        assertEquals(1, dell.size());
        assertEquals("Notebook Gamer", dell.get(0).getNome());
        assertEquals(1, logitech.size());
    }

    @Test
    @DisplayName("Deve buscar produtos por nome (case insensitive)")
    void deveBuscarProdutosPorNome() {
        entityManager.persist(notebook);
        entityManager.persist(mouse);
        entityManager.persist(teclado);
        entityManager.flush();

        List<Produto> resultadoMinusculo = produtoRepository.findByNomeContainingIgnoreCase("gamer");
        List<Produto> resultadoMaiusculo = produtoRepository.findByNomeContainingIgnoreCase("GAMER");
        List<Produto> resultadoParcial = produtoRepository.findByNomeContainingIgnoreCase("note");

        assertEquals(2, resultadoMinusculo.size());
        assertEquals(2, resultadoMaiusculo.size());
        assertEquals(1, resultadoParcial.size());
    }

    @Test
    @DisplayName("Deve buscar produtos por faixa de preço")
    void deveBuscarProdutosPorFaixaDePreco() {
        entityManager.persist(notebook);
        entityManager.persist(mouse);
        entityManager.persist(monitor);
        entityManager.flush();

        List<Produto> entre200e1000 = produtoRepository.findByPrecoBetween(
                new BigDecimal("200.00"),
                new BigDecimal("1000.00")
        );

        List<Produto> entre1000e6000 = produtoRepository.findByPrecoBetween(
                new BigDecimal("1000.00"),
                new BigDecimal("6000.00")
        );

        assertEquals(1, entre200e1000.size());
        assertEquals("Mouse Gamer RGB", entre200e1000.get(0).getNome());
        assertEquals(2, entre1000e6000.size());
    }

    @Test
    @DisplayName("Deve buscar produtos com estoque maior que quantidade")
    void deveBuscarProdutosComEstoque() {
        entityManager.persist(notebook); // estoque: 15
        entityManager.persist(mouse);    // estoque: 50
        entityManager.persist(teclado);  // estoque: 0
        entityManager.flush();

        List<Produto> comEstoque = produtoRepository.findByEstoqueGreaterThan(0);
        List<Produto> estoqueAlto = produtoRepository.findByEstoqueGreaterThan(20);

        assertEquals(2, comEstoque.size());
        assertEquals(1, estoqueAlto.size());
        assertEquals("Mouse Gamer RGB", estoqueAlto.get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar produto por SKU")
    void deveBuscarProdutoPorSku() {
        entityManager.persist(notebook);
        entityManager.persist(mouse);
        entityManager.flush();

        Optional<Produto> encontrado = produtoRepository.findByCodigo("NOT-DELL-001");

        assertTrue(encontrado.isPresent());
        assertEquals("Notebook Gamer", encontrado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar vazio quando SKU não existe")
    void deveRetornarVazioQuandoSkuNaoExiste() {
        entityManager.persist(notebook);
        entityManager.flush();

        Optional<Produto> encontrado = produtoRepository.findByCodigo("SKU-INEXISTENTE");

        assertTrue(encontrado.isEmpty());
    }

    @Test
    @DisplayName("Deve listar produtos disponíveis (ativos e com estoque)")
    void deveListarProdutosDisponiveis() {
        entityManager.persist(notebook); // ativo, estoque 15
        entityManager.persist(mouse);    // ativo, estoque 50
        entityManager.persist(teclado);  // inativo, estoque 0

        Produto semEstoque = new Produto();
        semEstoque.setNome("Produto Sem Estoque");
        semEstoque.setPreco(new BigDecimal("100.00"));
        semEstoque.setEstoque(0);
        entityManager.persist(semEstoque);

        entityManager.flush();

        List<Produto> disponiveis = produtoRepository.findProdutosDisponiveis();

        assertEquals(2, disponiveis.size());
    }

    @Test
    @DisplayName("Deve buscar produtos com múltiplos filtros")
    void deveBuscarComMultiplosFiltros() {
        entityManager.persist(notebook);
        entityManager.persist(mouse);
        entityManager.persist(monitor);
        entityManager.flush();

        // Filtro 1: Categoria Eletrônicos, marca Dell
        List<Produto> filtro1 = produtoRepository.buscarComFiltros(
                "Eletrônicos",
                "Dell",
                null,
                null
        );

        // Filtro 2: Preço entre 1000 e 3000
        List<Produto> filtro2 = produtoRepository.buscarComFiltros(
                null,
                null,
                new BigDecimal("1000.00"),
                new BigDecimal("3000.00")
        );

        // Filtro 3: Eletrônicos entre 1000 e 6000
        List<Produto> filtro3 = produtoRepository.buscarComFiltros(
                "Eletrônicos",
                null,
                new BigDecimal("1000.00"),
                new BigDecimal("6000.00")
        );

        assertEquals(1, filtro1.size());
        assertEquals("Notebook Gamer", filtro1.get(0).getNome());

        assertEquals(1, filtro2.size());
        assertEquals("Monitor UltraWide", filtro2.get(0).getNome());

        assertEquals(2, filtro3.size());
    }

    @Test
    @DisplayName("Deve buscar com filtros nulos (retornar todos ativos)")
    void deveBuscarComFiltrosNulos() {
        entityManager.persist(notebook);
        entityManager.persist(mouse);
        entityManager.persist(teclado);
        entityManager.flush();

        List<Produto> todos = produtoRepository.buscarComFiltros(null, null, null, null);

        assertEquals(3, todos.size());
    }

    @Test
    @DisplayName("Deve atualizar produto existente")
    void deveAtualizarProdutoExistente() {
        entityManager.persist(notebook);
        entityManager.flush();

        Produto produtoParaAtualizar = produtoRepository.findById(notebook.getId()).get();
        produtoParaAtualizar.setPreco(new BigDecimal("5500.00"));
        produtoParaAtualizar.setEstoque(20);

        Produto atualizado = produtoRepository.save(produtoParaAtualizar);

        assertEquals(new BigDecimal("5500.00"), atualizado.getPreco());
        assertEquals(20, atualizado.getEstoque());
    }

    @Test
    @DisplayName("Deve deletar produto")
    void deveDeletarProduto() {
        entityManager.persist(notebook);
        entityManager.flush();

        Long id = notebook.getId();
        produtoRepository.deleteById(id);

        Optional<Produto> deletado = produtoRepository.findById(id);
        assertTrue(deletado.isEmpty());
    }

    @Test
    @DisplayName("Deve verificar se SKU é único")
    void deveVerificarSeSkuEhUnico() {
        entityManager.persist(notebook);
        entityManager.flush();

        Optional<Produto> produto1 = produtoRepository.findByCodigo("NOT-DELL-001");
        Optional<Produto> produto2 = produtoRepository.findByCodigo("NOT-DELL-001");

        assertTrue(produto1.isPresent());
        assertTrue(produto2.isPresent());
        assertEquals(produto1.get().getId(), produto2.get().getId());
    }

    @Test
    @DisplayName("Deve buscar produtos por categoria case sensitive")
    void deveBuscarPorCategoriaCaseSensitive() {
        entityManager.persist(notebook);
        entityManager.flush();

        List<Produto> encontrados = produtoRepository.findByCategoria("Eletrônicos");
        List<Produto> naoEncontrados = produtoRepository.findByCategoria("eletronicos");

        assertEquals(1, encontrados.size());
        assertEquals(0, naoEncontrados.size());
    }

    @Test
    @DisplayName("Deve contar total de produtos")
    void deveContarTotalDeProdutos() {
        entityManager.persist(notebook);
        entityManager.persist(mouse);
        entityManager.persist(teclado);
        entityManager.flush();

        long total = produtoRepository.count();

        assertEquals(3, total);
    }

    @Test
    @DisplayName("Deve verificar se produto existe por ID")
    void deveVerificarSeProdutoExistePorId() {
        entityManager.persist(notebook);
        entityManager.flush();

        boolean existe = produtoRepository.existsById(notebook.getId());
        boolean naoExiste = produtoRepository.existsById(999L);

        assertTrue(existe);
        assertFalse(naoExiste);
    }

    @Test
    @DisplayName("Deve listar produtos ordenados por preço")
    void deveListarProdutosOrdenadosPorPreco() {
        entityManager.persist(notebook); // 5000
        entityManager.persist(mouse);    // 250
        entityManager.persist(monitor);  // 2000
        entityManager.flush();

        List<Produto> todos = produtoRepository.findAll();
        todos.sort((p1, p2) -> p1.getPreco().compareTo(p2.getPreco()));

        assertEquals("Mouse Gamer RGB", todos.get(0).getNome());
        assertEquals("Monitor UltraWide", todos.get(1).getNome());
        assertEquals("Notebook Gamer", todos.get(2).getNome());
    }
}