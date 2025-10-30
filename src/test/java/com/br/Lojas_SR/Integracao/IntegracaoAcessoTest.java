package com.br.Lojas_SR.Integracao;

import com.br.Lojas_SR.Entity.Usuario;
import com.br.Lojas_SR.Service.AcessoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Testes de Integração - Acesso e Autenticação")
class IntegracaoAcessoTest {

    @Autowired
    private AcessoService acessoService;

    @Test
    @DisplayName("Deve impedir registro com email duplicado")
    void deveImpedirRegistroComEmailDuplicado() {
        String email = "duplicado" + System.currentTimeMillis() + "@email.com";

        Usuario usuario1 = new Usuario();
        usuario1.setNome("Usuário 1");
        usuario1.setEmail(email);
        usuario1.setSenha("senha123");

        acessoService.registrar(usuario1);

        Usuario usuario2 = new Usuario();
        usuario2.setNome("Usuário 2");
        usuario2.setEmail(email); // Email duplicado
        usuario2.setSenha("senha456");

        assertThrows(RuntimeException.class, () -> {
            acessoService.registrar(usuario2);
        });
    }

    @Test
    @DisplayName("Deve impedir registro com CPF duplicado")
    void deveImpedirRegistroComCpfDuplicado() {
        String cpf = "11122233344";

        Usuario usuario1 = new Usuario();
        usuario1.setNome("Usuário 1");
        usuario1.setEmail("user1" + System.currentTimeMillis() + "@email.com");
        usuario1.setSenha("senha123");
        usuario1.setCpf(cpf);

        acessoService.registrar(usuario1);

        Usuario usuario2 = new Usuario();
        usuario2.setNome("Usuário 2");
        usuario2.setEmail("user2" + System.currentTimeMillis() + "@email.com");
        usuario2.setSenha("senha456");
        usuario2.setCpf(cpf); // CPF duplicado

        assertThrows(RuntimeException.class, () -> {
            acessoService.registrar(usuario2);
        });
    }

    @Test
    @DisplayName("Deve atualizar dados do usuário mantendo email único")
    void deveAtualizarDadosDoUsuarioMantendoEmailUnico() {
        Usuario usuario = new Usuario();
        usuario.setNome("Atualizar Dados");
        usuario.setEmail("atualizar" + System.currentTimeMillis() + "@email.com");
        usuario.setSenha("senha123");
        usuario.setTelefone("11999999999");

        Usuario registrado = acessoService.registrar(usuario);

        // Atualizar dados
        Usuario dadosAtualizados = new Usuario();
        dadosAtualizados.setNome("Nome Atualizado");
        dadosAtualizados.setTelefone("11988888888");
        dadosAtualizados.setCidade("São Paulo");

        Usuario atualizado = acessoService.atualizar(registrado.getId(), dadosAtualizados);

        assertEquals("Nome Atualizado", atualizado.getNome());
        assertEquals("11988888888", atualizado.getTelefone());
        assertEquals("São Paulo", atualizado.getCidade());
        assertEquals(registrado.getEmail(), atualizado.getEmail()); // Email não muda
    }
}
