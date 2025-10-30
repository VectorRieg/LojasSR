package com.br.Lojas_SR.Repository;

import com.br.Lojas_SR.Entity.Carrinho;
import com.br.Lojas_SR.Entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarrinhoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    private Carrinho carrinho;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senha123");
        entityManager.persist(usuario);

        carrinho = new Carrinho();
        carrinho.setUsuario(usuario);
        carrinho.setItens(new ArrayList<>());
    }

    @Test
    void deveSalvarCarrinho() {
        Carrinho salvo = carrinhoRepository.save(carrinho);

        assertNotNull(salvo.getId());
        assertEquals(usuario.getId(), salvo.getUsuario().getId());
    }

    @Test
    void deveBuscarCarrinhoPorUsuarioId() {
        entityManager.persist(carrinho);
        entityManager.flush();

        Optional<Carrinho> encontrado = carrinhoRepository.findByUsuarioId(usuario.getId());

        assertTrue(encontrado.isPresent());
        assertEquals(usuario.getId(), encontrado.get().getUsuario().getId());
    }

    @Test
    void deveVerificarSeUsuarioTemCarrinho() {
        entityManager.persist(carrinho);
        entityManager.flush();

        boolean existe = carrinhoRepository.existsByUsuarioId(usuario.getId());

        assertTrue(existe);
    }
}