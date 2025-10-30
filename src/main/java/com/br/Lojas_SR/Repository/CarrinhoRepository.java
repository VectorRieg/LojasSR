package com.br.Lojas_SR.Repository;

import com.br.Lojas_SR.Entity.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    // Buscar carrinho por ID do usuário
    Optional<Carrinho> findByUsuarioId(Long usuarioId);

    // Verificar se usuário já tem carrinho
    boolean existsByUsuarioId(Long usuarioId);

    // Deletar carrinho por usuário
    void deleteByUsuarioId(Long usuarioId);
}