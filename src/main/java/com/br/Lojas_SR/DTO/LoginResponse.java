package com.br.Lojas_SR.DTO;

public class LoginResponse {
    private String token;
    private Long userId;
    private String nome;
    private String email;

    public LoginResponse() {}

    public LoginResponse(String token, Long userId, String nome, String email) {
        this.token = token;
        this.userId = userId;
        this.nome = nome;
        this.email = email;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
