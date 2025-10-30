package com.br.Lojas_SR.Service;

import com.br.Lojas_SR.Entity.Carrinho;
import com.br.Lojas_SR.Entity.Usuario;
import com.br.Lojas_SR.Repository.CarrinhoRepository;
import com.br.Lojas_SR.Repository.AcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private AcessoRepository acessoRepository;

    // Criar carrinho para usuário
    public Carrinho criar(Long usuarioId) {
        // Verificar se usuário existe
        Usuario usuario = acessoRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verificar se usuário já tem carrinho
        if (carrinhoRepository.existsByUsuarioId(usuarioId)) {
            throw new RuntimeException("Usuário já possui um carrinho");
        }

        Carrinho carrinho = new Carrinho();
        carrinho.setUsuario(usuario);
        carrinho.setItens(new ArrayList<>());

        return carrinhoRepository.save(carrinho);
    }

    // Buscar carrinho por usuário
    public Carrinho buscarPorUsuario(Long usuarioId) {
        return carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));
    }

    // Buscar carrinho por ID
    public Carrinho buscarPorId(Long id) {
        return carrinhoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));
    }

    // Buscar ou criar carrinho
    public Carrinho buscarOuCriar(Long usuarioId) {
        return carrinhoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> criar(usuarioId));
    }

    // Calcular total do carrinho
    public BigDecimal calcularTotal(Long id) {
        Carrinho carrinho = buscarPorId(id);
        return carrinho.calcularTotal();
    }

    // Limpar carrinho (remover todos os itens)
    public void limpar(Long id) {
        Carrinho carrinho = buscarPorId(id);
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);
    }

    // Deletar carrinho
    public void deletar(Long id) {
        if (!carrinhoRepository.existsById(id)) {
            throw new RuntimeException("Carrinho não encontrado");
        }
        carrinhoRepository.deleteById(id);
    }
}