package com.br.Lojas_SR.Service;

import com.br.Lojas_SR.DTO.LoginResponse;
import com.br.Lojas_SR.DTO.RegistroResponse;
import com.br.Lojas_SR.Entity.Usuario;
import com.br.Lojas_SR.Repository.AcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.List;

@Service
public class AcessoService {

    @Autowired
    private AcessoRepository acessoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registrar novo usuário
    public RegistroResponse registrar(Usuario usuario) {
        // Validar se email já existe
        if (acessoRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        // Validar se CPF já existe
        if (usuario.getCpf() != null && acessoRepository.existsByCpf(usuario.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        // Guardar senha original para gerar token
        String senhaOriginal = usuario.getSenha();

        // Criptografar a senha
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setAtivo(true);

        Usuario usuarioSalvo = acessoRepository.save(usuario);

        // Gerar token (usando Basic Auth por enquanto)
        String token = gerarToken(usuarioSalvo.getEmail(), senhaOriginal);

        // Criar resposta
        RegistroResponse.UsuarioDTO usuarioDTO = new RegistroResponse.UsuarioDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail()
        );

        return new RegistroResponse(token, usuarioDTO);
    }

    // Login
    public LoginResponse login(String email, String senha) {
        Usuario usuario = acessoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Validar a senha
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }

        // Verificar se usuário está ativo
        if (usuario.getAtivo() != null && !usuario.getAtivo()) {
            throw new RuntimeException("Usuário inativo");
        }

        // Gerar token (usando Basic Auth por enquanto)
        String token = gerarToken(email, senha);

        return new LoginResponse(token, usuario.getId(), usuario.getNome(), usuario.getEmail());
    }

    // Gerar token Basic Auth (em produção, usar JWT)
    private String gerarToken(String email, String senha) {
        String credentials = email + ":" + senha;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    // Validar token
    public Usuario validarToken() {
        // Como estamos usando Basic Auth com Spring Security,
        // o usuário autenticado já está disponível no SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return buscarPorEmail(email);
    }

    // Buscar usuário por ID
    public Usuario buscarPorId(Long id) {
        return acessoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // Buscar por email
    public Usuario buscarPorEmail(String email) {
        return acessoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // Listar todos usuários
    public List<Usuario> listarTodos() {
        return acessoRepository.findAll();
    }

    // Listar usuários ativos
    public List<Usuario> listarAtivos() {
        return acessoRepository.findByAtivo(true);
    }

    // Atualizar usuário
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario usuario = buscarPorId(id);

        // Atualizar apenas campos permitidos
        if (usuarioAtualizado.getNome() != null) {
            usuario.setNome(usuarioAtualizado.getNome());
        }
        if (usuarioAtualizado.getTelefone() != null) {
            usuario.setTelefone(usuarioAtualizado.getTelefone());
        }
        if (usuarioAtualizado.getCep() != null) {
            usuario.setCep(usuarioAtualizado.getCep());
            usuario.setRua(usuarioAtualizado.getRua());
            usuario.setNumero(usuarioAtualizado.getNumero());
            usuario.setComplemento(usuarioAtualizado.getComplemento());
            usuario.setBairro(usuarioAtualizado.getBairro());
            usuario.setCidade(usuarioAtualizado.getCidade());
            usuario.setEstado(usuarioAtualizado.getEstado());
        }

        return acessoRepository.save(usuario);
    }

    // Deletar usuário (desativar)
    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        acessoRepository.save(usuario);
    }

    // Deletar permanentemente
    public void deletarPermanente(Long id) {
        acessoRepository.deleteById(id);
    }
}
