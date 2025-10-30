package com.br.Lojas_SR.Repository;

import com.br.Lojas_SR.Entity.MetodoPagamento;
import com.br.Lojas_SR.Entity.Pagamento;
import com.br.Lojas_SR.Entity.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    // Buscar pagamento por ID do pedido
    Optional<Pagamento> findByPedidoId(Long pedidoId);

    // Listar pagamentos por usuário (através do pedido)
    @Query("SELECT p FROM Pagamento p WHERE p.pedido.usuario.id = :usuarioId")
    List<Pagamento> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Listar pagamentos pendentes
    @Query("SELECT p FROM Pagamento p WHERE p.status = 'PENDENTE' OR p.status = 'PROCESSANDO'")
    List<Pagamento> findPagamentosPendentes();

    // Verificar se pedido já tem pagamento
    boolean existsByPedidoId(Long pedidoId);

    List<Pagamento> findByStatus(StatusPagamento statusPagamento);
}