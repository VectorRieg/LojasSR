package com.br.Lojas_SR.Service;

import com.br.Lojas_SR.Entity.Carrinho;
import com.br.Lojas_SR.Entity.Usuario;
import com.br.Lojas_SR.Repository.AcessoRepository;
import com.br.Lojas_SR.Repository.CarrinhoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrinhoServiceTest {

    @Mock
    private CarrinhoRepository carrinhoRepository;

    @Mock
    private AcessoRepository acessoRepository;

    @InjectMocks
    private CarrinhoService carrinhoService;

    private Carrinho carrinho;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");

        carrinho = new Carrinho();
        carrinho.setId(1L);
        carrinho.setUsuario(usuario);
        carrinho.setItens(new ArrayList<>());
    }

    @Test
    void deveCriarCarrinhoComSucesso() {
        when(acessoRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carrinhoRepository.existsByUsuarioId(1L)).thenReturn(false);
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        Carrinho resultado = carrinhoService.criar(1L);

        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        verify(carrinhoRepository, times(1)).save(any(Carrinho.class));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(acessoRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carrinhoService.criar(1L);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioJaTemCarrinho() {
        when(acessoRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carrinhoRepository.existsByUsuarioId(1L)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carrinhoService.criar(1L);
        });

        assertEquals("Usuário já possui um carrinho", exception.getMessage());
    }

    @Test
    void deveBuscarCarrinhoPorUsuario() {
        when(carrinhoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrinho));

        Carrinho resultado = carrinhoService.buscarPorUsuario(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(carrinhoRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void deveBuscarCarrinhoPorId() {
        when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(carrinho));

        Carrinho resultado = carrinhoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(carrinhoRepository, times(1)).findById(1L);
    }

    @Test
    void deveCalcularTotal() {
        when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(carrinho));

        BigDecimal total = carrinhoService.calcularTotal(1L);

        assertNotNull(total);
        assertEquals(BigDecimal.ZERO, total);
        verify(carrinhoRepository, times(1)).findById(1L);
    }

    @Test
    void deveLimparCarrinho() {
        when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(carrinho));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        carrinhoService.limpar(1L);

        assertTrue(carrinho.getItens().isEmpty());
        verify(carrinhoRepository, times(1)).save(carrinho);
    }

    @Test
    void deveDeletarCarrinho() {
        when(carrinhoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(carrinhoRepository).deleteById(1L);

        carrinhoService.deletar(1L);

        verify(carrinhoRepository, times(1)).deleteById(1L);
    }
}