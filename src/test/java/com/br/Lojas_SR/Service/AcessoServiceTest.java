package com.br.Lojas_SR.Service;

import com.br.Lojas_SR.Config.AcessoConfig;
import com.br.Lojas_SR.Entity.Usuario;
import com.br.Lojas_SR.Repository.AcessoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcessoServiceTest {

    @Mock
    private AcessoRepository acessoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AcessoConfig acessoConfig;

    @InjectMocks
    private AcessoService acessoService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senha123");
        usuario.setCpf("12345678900");
    }

    @Test
    void deveRegistrarUsuarioComSucesso() {
        when(acessoRepository.existsByEmail(anyString())).thenReturn(false);
        when(acessoRepository.existsByCpf(anyString())).thenReturn(false);
        when(acessoRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = acessoService.registrar(usuario);

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        verify(acessoRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        when(acessoRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            acessoService.registrar(usuario);
        });

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(acessoRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaExiste() {
        when(acessoRepository.existsByEmail(anyString())).thenReturn(false);
        when(acessoRepository.existsByCpf(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            acessoService.registrar(usuario);
        });

        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(acessoRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveFazerLoginComSucesso() {
        when(acessoRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        String token = acessoService.login("joao@email.com", "senha123");

        assertNotNull(token);
        verify(acessoRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(acessoRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            acessoService.login("inexistente@email.com", "senha123");
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioInativo() {
        when(acessoRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            acessoService.login("joao@email.com", "senha123");
        });

        assertEquals("Usuário inativo", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        when(acessoRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            acessoService.login("joao@email.com", "senhaErrada");
        });

        assertEquals("Senha incorreta", exception.getMessage());
    }

    @Test
    void deveBuscarUsuarioPorId() {
        when(acessoRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = acessoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(acessoRepository, times(1)).findById(1L);
    }

    @Test
    void deveListarTodosUsuarios() {
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(acessoRepository.findAll()).thenReturn(usuarios);

        List<Usuario> resultado = acessoService.listarTodos();

        assertEquals(1, resultado.size());
        verify(acessoRepository, times(1)).findAll();
    }

    @Test
    void deveAtualizarUsuario() {
        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setNome("João Silva Atualizado");
        usuarioAtualizado.setTelefone("11999999999");

        when(acessoRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(acessoRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = acessoService.atualizar(1L, usuarioAtualizado);

        assertNotNull(resultado);
        verify(acessoRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveDeletarUsuario() {
        when(acessoRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(acessoRepository.save(any(Usuario.class))).thenReturn(usuario);

        acessoService.deletar(1L);

        verify(acessoRepository, times(1)).save(usuario);
    }
}