package com.br.Lojas_SR.Controller;

import com.br.Lojas_SR.Entity.Endereco;
import com.br.Lojas_SR.Entity.Pedido;
import com.br.Lojas_SR.Entity.Usuario;
import com.br.Lojas_SR.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Dados do Perfil
    @GetMapping("/perfil")
    public ResponseEntity<Usuario> getDados() {
        Usuario usuario = usuarioService.getDados();
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/perfil")
    public ResponseEntity<Usuario> atualizarDados(@RequestBody Usuario dados) {
        Usuario usuario = usuarioService.atualizarDados(dados);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/perfil/senha")
    public ResponseEntity<Void> alterarSenha(@RequestBody AlterarSenhaRequest request) {
        usuarioService.alterarSenha(request.getSenhaAtual(), request.getNovaSenha());
        return ResponseEntity.ok().build();
    }

    // Gerenciamento de Endereços
    @GetMapping("/perfil/enderecos")
    public ResponseEntity<List<Endereco>> getEnderecos() {
        List<Endereco> enderecos = usuarioService.getEnderecos();
        return ResponseEntity.ok(enderecos);
    }

    @GetMapping("/perfil/enderecos/{id}")
    public ResponseEntity<Endereco> getEndereco(@PathVariable Long id) {
        Endereco endereco = usuarioService.getEndereco(id);
        return ResponseEntity.ok(endereco);
    }

    @PostMapping("/perfil/enderecos")
    public ResponseEntity<Endereco> adicionarEndereco(@RequestBody Endereco endereco) {
        Endereco novoEndereco = usuarioService.adicionarEndereco(endereco);
        return ResponseEntity.ok(novoEndereco);
    }

    @PutMapping("/perfil/enderecos/{id}")
    public ResponseEntity<Endereco> atualizarEndereco(@PathVariable Long id, @RequestBody Endereco endereco) {
        Endereco enderecoAtualizado = usuarioService.atualizarEndereco(id, endereco);
        return ResponseEntity.ok(enderecoAtualizado);
    }

    @DeleteMapping("/perfil/enderecos/{id}")
    public ResponseEntity<Void> removerEndereco(@PathVariable Long id) {
        usuarioService.removerEndereco(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/perfil/enderecos/{id}/principal")
    public ResponseEntity<Void> definirEnderecoPrincipal(@PathVariable Long id) {
        usuarioService.definirEnderecoPrincipal(id);
        return ResponseEntity.ok().build();
    }

    // Gerenciamento de Pedidos
    @GetMapping("/perfil/pedidos")
    public ResponseEntity<List<Pedido>> getPedidos() {
        List<Pedido> pedidos = usuarioService.getPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/perfil/pedidos/{id}")
    public ResponseEntity<Pedido> getPedido(@PathVariable Long id) {
        Pedido pedido = usuarioService.getPedido(id);
        return ResponseEntity.ok(pedido);
    }

    @PostMapping("/perfil/pedidos/{id}/cancelar")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
        usuarioService.cancelarPedido(id);
        return ResponseEntity.ok().build();
    }

    // Classe interna para request de alterar senha
    public static class AlterarSenhaRequest {
        private String senhaAtual;
        private String novaSenha;

        public String getSenhaAtual() { return senhaAtual; }
        public void setSenhaAtual(String senhaAtual) { this.senhaAtual = senhaAtual; }
        public String getNovaSenha() { return novaSenha; }
        public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }
    }
}
