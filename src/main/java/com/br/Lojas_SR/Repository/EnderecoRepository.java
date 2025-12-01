package com.br.Lojas_SR.Repository;

import com.br.Lojas_SR.Entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findByUsuarioId(Long usuarioId);
    Optional<Endereco> findByIdAndUsuarioId(Long id, Long usuarioId);
    Optional<Endereco> findByUsuarioIdAndPrincipalTrue(Long usuarioId);
}
