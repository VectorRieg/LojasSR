package com.br.Lojas_SR.Repository;

import com.br.Lojas_SR.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcessoRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuário por email (para login)
    Optional<Usuario> findByEmail(String email);

    // Verificar se email já existe (para registro)
    boolean existsByEmail(String email);

    // Buscar por CPF
    Optional<Usuario> findByCpf(String cpf);

    // Verificar se CPF já existe
    boolean existsByCpf(String cpf);

    // Buscar usuários ativos
    List<Usuario> findByAtivo(Boolean ativo);
}