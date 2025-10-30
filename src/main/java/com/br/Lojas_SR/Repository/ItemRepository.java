package com.br.Lojas_SR.Repository;

import com.br.Lojas_SR.Entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Listar todos os itens de um carrinho
    List<Item> findByCarrinhoId(Long carrinhoId);

    // Buscar item específico no carrinho
    Optional<Item> findByCarrinhoIdAndProdutoId(Long carrinhoId, Long produtoId);

    // Deletar todos os itens de um carrinho
    void deleteByCarrinhoId(Long carrinhoId);

    // Contar quantos itens tem no carrinho
    Long countByCarrinhoId(Long carrinhoId);

    // Verificar se produto já está no carrinho
    boolean existsByCarrinhoIdAndProdutoId(Long carrinhoId, Long produtoId);
}