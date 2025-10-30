package com.br.Lojas_SR.Controller;

import com.br.Lojas_SR.Entity.Usuario;
import com.br.Lojas_SR.Service.AcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/acesso")
@CrossOrigin(origins = "*")
public class AcessoController {

    @Autowired
    private AcessoService acessoService;

    // Registro de novo usuario
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        Usuario novoUsuario = acessoService.registrar(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = acessoService.login(request.getEmail(), request.getSenha());
        return ResponseEntity.ok(token);
    }

    // Busca usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario usuario = acessoService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    // Atualiza dados do usuário
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        Usuario atualizado = acessoService.atualizar(id, usuario);
        return ResponseEntity.ok(atualizado);
    }

    // Deleta usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        acessoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Classe interna para request de login
    public static class LoginRequest {
        private String email;
        private String senha;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }
}
