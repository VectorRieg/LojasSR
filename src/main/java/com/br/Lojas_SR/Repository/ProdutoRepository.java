package com.br.Lojas_SR.Repository;

import com.br.Lojas_SR.Entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Buscar produtos ativos
    List<Produto> findByAtivo(Boolean ativo);

    // Buscar produtos em destaque
    List<Produto> findByDestaque(Boolean destaque);

    // Buscar produtos em promoção
    List<Produto> findByEmPromocao(Boolean emPromocao);

    // Buscar por categoria
    List<Produto> findByCategoria(String categoria);

    // Buscar por marca
    List<Produto> findByMarca(String marca);

    // Buscar por nome (case insensitive)
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Buscar por faixa de preço
    List<Produto> findByPrecoBetween(BigDecimal precoMin, BigDecimal precoMax);

    // Buscar por SKU
    Optional<Produto> findByCodigo(String codigo);

    // Buscar produtos ativos e com estoque
    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.estoque > 0")
    List<Produto> findProdutosDisponiveis();

    // Buscar por múltiplos filtros
    @Query("SELECT p FROM Produto p WHERE " +
            "(:categoria IS NULL OR p.categoria = :categoria) AND " +
            "(:marca IS NULL OR p.marca = :marca) AND " +
            "(:precoMin IS NULL OR p.preco >= :precoMin) AND " +
            "(:precoMax IS NULL OR p.preco <= :precoMax) AND " +
            "p.ativo = true")
    List<Produto> buscarComFiltros(
            @Param("categoria") String categoria,
            @Param("marca") String marca,
            @Param("precoMin") BigDecimal precoMin,
            @Param("precoMax") BigDecimal precoMax
    );

    List<Produto> findByEstoqueGreaterThan(Integer quantidade);
}