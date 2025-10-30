package com.br.Lojas_SR.Repository;

import com.br.Lojas_SR.Entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AcessoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AcessoRepository acessoRepository;

    private Usuario joao;
    private Usuario maria;
    private Usuario pedro;
    private Usuario ana;

    @BeforeEach
    void setUp() {
        joao = new Usuario();
        joao.setNome("João Silva");
        joao.setEmail("joao@email.com");
        joao.setSenha("senha123");
        joao.setCpf("00000000001");
        joao.setTelefone("00000000001");
        joao.setCep("01310-100");
        joao.setRua("Av. Paulista");
        joao.setNumero("1000");
        joao.setComplemento("Apto 101");
        joao.setBairro("Bela Vista");
        joao.setCidade("São Paulo");
        joao.setEstado("SP");

        maria = new Usuario();
        maria.setNome("Maria Santos");
        maria.setEmail("maria@email.com");
        maria.setSenha("senha456");
        maria.setCpf("00000000002");
        maria.setTelefone("00000000002");

        pedro = new Usuario();
        pedro.setNome("Pedro Oliveira");
        pedro.setEmail("pedro@email.com");
        pedro.setSenha("senha789");
        pedro.setCpf("00000000003");
        pedro.setTelefone("00000000003");

        ana = new Usuario();
        ana.setNome("Ana Costa");
        ana.setEmail("ana@email.com");
        ana.setSenha("senha321");
        ana.setCpf("00000000004");
        ana.setTelefone("00000000004");

    }

    @Test
    @DisplayName("Deve salvar usuário com sucesso")
    void deveSalvarUsuarioComSucesso() {
        Usuario salvo = acessoRepository.save(joao);

        assertNotNull(salvo.getId());
        assertEquals("João Silva", salvo.getNome());
        assertEquals("joao@email.com", salvo.getEmail());
        assertEquals("12345678900", salvo.getCpf());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void deveBuscarUsuarioPorId() {
        entityManager.persist(joao);
        entityManager.flush();

        Optional<Usuario> encontrado = acessoRepository.findById(joao.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("João Silva", encontrado.get().getNome());
        assertEquals("joao@email.com", encontrado.get().getEmail());
    }

    @Test
    @DisplayName("Deve retornar vazio quando usuário não existe")
    void deveRetornarVazioQuandoUsuarioNaoExiste() {
        Optional<Usuario> naoEncontrado = acessoRepository.findById(999L);

        assertTrue(naoEncontrado.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar usuário por email")
    void deveBuscarUsuarioPorEmail() {
        entityManager.persist(joao);
        entityManager.persist(maria);
        entityManager.flush();

        Optional<Usuario> encontrado = acessoRepository.findByEmail("joao@email.com");

        assertTrue(encontrado.isPresent());
        assertEquals("João Silva", encontrado.get().getNome());
        assertEquals("12345678900", encontrado.get().getCpf());
    }

    @Test
    @DisplayName("Deve retornar vazio quando email não existe")
    void deveRetornarVazioQuandoEmailNaoExiste() {
        entityManager.persist(joao);
        entityManager.flush();

        Optional<Usuario> naoEncontrado = acessoRepository.findByEmail("inexistente@email.com");

        assertTrue(naoEncontrado.isEmpty());
    }

    @Test
    @DisplayName("Deve verificar se email existe")
    void deveVerificarSeEmailExiste() {
        entityManager.persist(joao);
        entityManager.flush();

        boolean existe = acessoRepository.existsByEmail("joao@email.com");
        boolean naoExiste = acessoRepository.existsByEmail("outro@email.com");

        assertTrue(existe);
        assertFalse(naoExiste);
    }

    @Test
    @DisplayName("Email deve ser case sensitive")
    void emailDeveSerCaseSensitive() {
        entityManager.persist(joao);
        entityManager.flush();

        Optional<Usuario> minusculo = acessoRepository.findByEmail("joao@email.com");
        Optional<Usuario> maiusculo = acessoRepository.findByEmail("JOAO@EMAIL.COM");

        assertTrue(minusculo.isPresent());
        assertTrue(maiusculo.isEmpty()); // Email é case sensitive no banco
    }

    @Test
    @DisplayName("Deve buscar usuário por CPF")
    void deveBuscarUsuarioPorCpf() {
        entityManager.persist(joao);
        entityManager.persist(maria);
        entityManager.flush();

        Optional<Usuario> encontrado = acessoRepository.findByCpf("12345678900");

        assertTrue(encontrado.isPresent());
        assertEquals("João Silva", encontrado.get().getNome());
        assertEquals("joao@email.com", encontrado.get().getEmail());
    }

    @Test
    @DisplayName("Deve retornar vazio quando CPF não existe")
    void deveRetornarVazioQuandoCpfNaoExiste() {
        entityManager.persist(joao);
        entityManager.flush();

        Optional<Usuario> naoEncontrado = acessoRepository.findByCpf("00000000000");

        assertTrue(naoEncontrado.isEmpty());
    }

    @Test
    @DisplayName("Deve verificar se CPF existe")
    void deveVerificarSeCpfExiste() {
        entityManager.persist(joao);
        entityManager.flush();

        boolean existe = acessoRepository.existsByCpf("12345678900");
        boolean naoExiste = acessoRepository.existsByCpf("00000000000");

        assertTrue(existe);
        assertFalse(naoExiste);
    }

    @Test
    @DisplayName("CPF deve ser único por usuário")
    void cpfDeveSerUnicoPorUsuario() {
        entityManager.persist(joao);
        entityManager.flush();

        Optional<Usuario> usuario1 = acessoRepository.findByCpf("12345678900");
        Optional<Usuario> usuario2 = acessoRepository.findByCpf("12345678900");

        assertTrue(usuario1.isPresent());
        assertTrue(usuario2.isPresent());
        assertEquals(usuario1.get().getId(), usuario2.get().getId());
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosUsuarios() {
        entityManager.persist(joao);
        entityManager.persist(maria);
        entityManager.persist(pedro);
        entityManager.flush();

        List<Usuario> usuarios = acessoRepository.findAll();

        assertEquals(3, usuarios.size());
    }

    @Test
    @DisplayName("Deve atualizar dados do usuário")
    void deveAtualizarDadosDoUsuario() {
        entityManager.persist(joao);
        entityManager.flush();

        Usuario usuarioParaAtualizar = acessoRepository.findById(joao.getId()).get();
        usuarioParaAtualizar.setNome("João Silva Atualizado");
        usuarioParaAtualizar.setTelefone("11955555555");
        usuarioParaAtualizar.setCidade("Rio de Janeiro");

        Usuario atualizado = acessoRepository.save(usuarioParaAtualizar);

        assertEquals("João Silva Atualizado", atualizado.getNome());
        assertEquals("11955555555", atualizado.getTelefone());
        assertEquals("Rio de Janeiro", atualizado.getCidade());
    }

    @Test
    @DisplayName("Deve deletar usuário permanentemente")
    void deveDeletarUsuarioPermanentemente() {
        entityManager.persist(joao);
        entityManager.flush();

        Long id = joao.getId();
        acessoRepository.deleteById(id);

        Optional<Usuario> deletado = acessoRepository.findById(id);
        assertTrue(deletado.isEmpty());
    }

    @Test
    @DisplayName("Deve contar total de usuários")
    void deveContarTotalDeUsuarios() {
        entityManager.persist(joao);
        entityManager.persist(maria);
        entityManager.persist(pedro);
        entityManager.flush();

        long total = acessoRepository.count();

        assertEquals(3, total);
    }

    @Test
    @DisplayName("Deve verificar se usuário existe por ID")
    void deveVerificarSeUsuarioExistePorId() {
        entityManager.persist(joao);
        entityManager.flush();

        boolean existe = acessoRepository.existsById(joao.getId());
        boolean naoExiste = acessoRepository.existsById(999L);

        assertTrue(existe);
        assertFalse(naoExiste);
    }

    @Test
    @DisplayName("Deve salvar usuário sem endereço completo")
    void deveSalvarUsuarioSemEnderecoCompleto() {
        Usuario semEndereco = new Usuario();
        semEndereco.setNome("Carlos");
        semEndereco.setEmail("carlos@email.com");
        semEndereco.setSenha("senha");

        Usuario salvo = acessoRepository.save(semEndereco);

        assertNotNull(salvo.getId());
        assertNull(salvo.getCep());
        assertNull(salvo.getRua());
        assertNull(salvo.getCidade());
    }

    @Test
    @DisplayName("Deve salvar usuário sem CPF")
    void deveSalvarUsuarioSemCpf() {
        Usuario semCpf = new Usuario();
        semCpf.setNome("Fernanda");
        semCpf.setEmail("fernanda@email.com");
        semCpf.setSenha("senha");

        Usuario salvo = acessoRepository.save(semCpf);

        assertNotNull(salvo.getId());
        assertNull(salvo.getCpf());
    }

    @Test
    @DisplayName("Deve buscar usuário com todos os dados de endereço")
    void deveBuscarUsuarioComTodosDadosDeEndereco() {
        entityManager.persist(joao);
        entityManager.flush();

        Usuario encontrado = acessoRepository.findById(joao.getId()).get();

        assertNotNull(encontrado.getCep());
        assertEquals("01310-100", encontrado.getCep());
        assertEquals("Av. Paulista", encontrado.getRua());
        assertEquals("1000", encontrado.getNumero());
        assertEquals("Apto 101", encontrado.getComplemento());
        assertEquals("Bela Vista", encontrado.getBairro());
        assertEquals("São Paulo", encontrado.getCidade());
        assertEquals("SP", encontrado.getEstado());
    }

    @Test
    @DisplayName("Deve salvar múltiplos usuários em batch")
    void deveSalvarMultiplosUsuariosEmBatch() {
        List<Usuario> usuarios = List.of(joao, maria, pedro, ana);

        List<Usuario> salvos = acessoRepository.saveAll(usuarios);

        assertEquals(4, salvos.size());
        assertTrue(salvos.stream().allMatch(u -> u.getId() != null));
    }

    @Test
    @DisplayName("Deve deletar múltiplos usuários")
    void deveDeletarMultiplosUsuarios() {
        entityManager.persist(joao);
        entityManager.persist(maria);
        entityManager.persist(pedro);
        entityManager.flush();

        acessoRepository.deleteAll(List.of(joao, maria));

        long total = acessoRepository.count();
        assertEquals(1, total);
    }

    @Test
    @DisplayName("Deve validar email único ao salvar")
    void deveValidarEmailUnicoAoSalvar() {
        entityManager.persist(joao);
        entityManager.flush();

        // Tentar salvar outro usuário com mesmo email deve falhar
        Usuario duplicado = new Usuario();
        duplicado.setNome("Outro Usuário");
        duplicado.setEmail("joao@email.com"); // Email duplicado
        duplicado.setSenha("senha");

        assertThrows(Exception.class, () -> {
            acessoRepository.save(duplicado);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Deve permitir CPF nulo para múltiplos usuários")
    void devePermitirCpfNuloParaMultiplosUsuarios() {
        Usuario usuario1 = new Usuario();
        usuario1.setNome("User 1");
        usuario1.setEmail("user1@email.com");
        usuario1.setSenha("senha");
        usuario1.setCpf(null);

        Usuario usuario2 = new Usuario();
        usuario2.setNome("User 2");
        usuario2.setEmail("user2@email.com");
        usuario2.setSenha("senha");
        usuario2.setCpf(null);

        Usuario salvo1 = acessoRepository.save(usuario1);
        Usuario salvo2 = acessoRepository.save(usuario2);

        assertNotNull(salvo1.getId());
        assertNotNull(salvo2.getId());
        assertNull(salvo1.getCpf());
        assertNull(salvo2.getCpf());
    }

    @Test
    @DisplayName("Deve buscar usuário e validar todos os campos")
    void deveBuscarUsuarioEValidarTodosCampos() {
        entityManager.persist(joao);
        entityManager.flush();

        Usuario encontrado = acessoRepository.findByEmail("joao@email.com").get();

        // Dados básicos
        assertNotNull(encontrado.getId());
        assertEquals("João Silva", encontrado.getNome());
        assertEquals("joao@email.com", encontrado.getEmail());
        assertEquals("senha123", encontrado.getSenha());

        // Documentos
        assertEquals("12345678900", encontrado.getCpf());
        assertEquals("11999999999", encontrado.getTelefone());

        // Endereço
        assertEquals("01310-100", encontrado.getCep());
        assertEquals("Av. Paulista", encontrado.getRua());
        assertEquals("1000", encontrado.getNumero());
        assertEquals("Apto 101", encontrado.getComplemento());
        assertEquals("Bela Vista", encontrado.getBairro());
        assertEquals("São Paulo", encontrado.getCidade());
        assertEquals("SP", encontrado.getEstado());
    }

}