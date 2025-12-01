package com.br.Lojas_SR.Service;

import com.br.Lojas_SR.Entity.Endereco;
import com.br.Lojas_SR.Entity.Pedido;
import com.br.Lojas_SR.Entity.Usuario;
import com.br.Lojas_SR.Repository.AcessoRepository;
import com.br.Lojas_SR.Repository.EnderecoRepository;
import com.br.Lojas_SR.Repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private AcessoRepository acessoRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Obter usuário autenticado
    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return acessoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // Dados do Perfil
    public Usuario getDados() {
        return getUsuarioAutenticado();
    }

    public Usuario atualizarDados(Usuario dadosAtualizados) {
        Usuario usuario = getUsuarioAutenticado();

        if (dadosAtualizados.getNome() != null) {
            usuario.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getTelefone() != null) {
            usuario.setTelefone(dadosAtualizados.getTelefone());
        }
        if (dadosAtualizados.getCpf() != null) {
            usuario.setCpf(dadosAtualizados.getCpf());
        }

        return acessoRepository.save(usuario);
    }

    public void alterarSenha(String senhaAtual, String novaSenha) {
        Usuario usuario = getUsuarioAutenticado();

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        acessoRepository.save(usuario);
    }

    // Gerenciamento de Endereços
    public List<Endereco> getEnderecos() {
        Usuario usuario = getUsuarioAutenticado();
        return enderecoRepository.findByUsuarioId(usuario.getId());
    }

    public Endereco getEndereco(Long id) {
        Usuario usuario = getUsuarioAutenticado();
        return enderecoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
    }

    public Endereco adicionarEndereco(Endereco endereco) {
        Usuario usuario = getUsuarioAutenticado();
        endereco.setUsuario(usuario);

        // Se é o primeiro endereço, marca como principal
        List<Endereco> enderecos = enderecoRepository.findByUsuarioId(usuario.getId());
        if (enderecos.isEmpty()) {
            endereco.setPrincipal(true);
        }

        return enderecoRepository.save(endereco);
    }

    public Endereco atualizarEndereco(Long id, Endereco enderecoAtualizado) {
        Endereco endereco = getEndereco(id);

        if (enderecoAtualizado.getCep() != null) {
            endereco.setCep(enderecoAtualizado.getCep());
        }
        if (enderecoAtualizado.getRua() != null) {
            endereco.setRua(enderecoAtualizado.getRua());
        }
        if (enderecoAtualizado.getNumero() != null) {
            endereco.setNumero(enderecoAtualizado.getNumero());
        }
        if (enderecoAtualizado.getComplemento() != null) {
            endereco.setComplemento(enderecoAtualizado.getComplemento());
        }
        if (enderecoAtualizado.getBairro() != null) {
            endereco.setBairro(enderecoAtualizado.getBairro());
        }
        if (enderecoAtualizado.getCidade() != null) {
            endereco.setCidade(enderecoAtualizado.getCidade());
        }
        if (enderecoAtualizado.getEstado() != null) {
            endereco.setEstado(enderecoAtualizado.getEstado());
        }

        return enderecoRepository.save(endereco);
    }

    public void removerEndereco(Long id) {
        Endereco endereco = getEndereco(id);
        enderecoRepository.delete(endereco);
    }

    public void definirEnderecoPrincipal(Long id) {
        Usuario usuario = getUsuarioAutenticado();
        Endereco endereco = getEndereco(id);

        // Remove principal de todos os endereços do usuário
        List<Endereco> enderecos = enderecoRepository.findByUsuarioId(usuario.getId());
        for (Endereco e : enderecos) {
            e.setPrincipal(false);
            enderecoRepository.save(e);
        }

        // Define o novo endereço principal
        endereco.setPrincipal(true);
        enderecoRepository.save(endereco);
    }

    // Gerenciamento de Pedidos
    public List<Pedido> getPedidos() {
        Usuario usuario = getUsuarioAutenticado();
        return pedidoRepository.findByUsuarioId(usuario.getId());
    }

    public Pedido getPedido(Long id) {
        Usuario usuario = getUsuarioAutenticado();
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Verifica se o pedido pertence ao usuário
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        return pedido;
    }

    public void cancelarPedido(Long id) {
        Pedido pedido = getPedido(id);
        // Aqui você pode adicionar lógica de cancelamento
        // Por exemplo, verificar status do pagamento, atualizar estoque, etc.
        throw new RuntimeException("Funcionalidade de cancelamento ainda não implementada");
    }
}
