package com.br.Lojas_SR.DTO;

public class RegistroResponse {
    private String token;
    private UsuarioDTO usuario;

    public RegistroResponse() {}

    public RegistroResponse(String token, UsuarioDTO usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UsuarioDTO getUsuario() { return usuario; }
    public void setUsuario(UsuarioDTO usuario) { this.usuario = usuario; }

    public static class UsuarioDTO {
        private Long userId;
        private String nome;
        private String email;

        public UsuarioDTO() {}

        public UsuarioDTO(Long userId, String nome, String email) {
            this.userId = userId;
            this.nome = nome;
            this.email = email;
        }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
