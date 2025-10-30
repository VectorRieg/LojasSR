package com.br.Lojas_SR.Service;

import com.br.Lojas_SR.Entity.Usuario;
import com.br.Lojas_SR.Repository.AcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AcessoService {

    @Autowired
    private AcessoRepository acessoRepository;

    // Registrar novo usuário
    public Usuario registrar(Usuario usuario) {
        // Validar se email já existe
        if (acessoRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        // Validar se CPF já existe
        if (usuario.getCpf() != null && acessoRepository.existsByCpf(usuario.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        // Aqui você deve criptografar a senha (BCrypt)
        // usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return acessoRepository.save(usuario);
    }

    // Login
    public String login(String email, String senha) {
        Usuario usuario = acessoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Aqui você deve validar a senha (BCrypt)
        // if (!passwordEncoder.matches(senha, usuario.getSenha())) {
        //     throw new RuntimeException("Senha incorreta");
        // }

        // Validação simples (trocar por BCrypt em produção)
        if (!usuario.getSenha().equals(senha)) {
            throw new RuntimeException("Senha incorreta");
        }

        // Aqui você deve gerar um JWT token
        // return jwtUtil.generateToken(usuario);

        return "token-jwt-aqui"; // Placeholder
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
        acessoRepository.save(usuario);
    }

    // Deletar permanentemente
    public void deletarPermanente(Long id) {
        acessoRepository.deleteById(id);
    }
}