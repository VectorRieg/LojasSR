package com.br.Lojas_SR.Config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utilitário para trabalhar com Basic Authentication
 * Substitui a antiga configuração JWT
 */
@Component
public class AcessoConfig {

    /**
     * Obtém o usuário autenticado no contexto atual
     * @return Email do usuário autenticado
     */
    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }

        return null;
    }

    /**
     * Verifica se há um usuário autenticado
     * @return true se houver usuário autenticado
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * Obtém os detalhes completos do usuário autenticado
     * @return UserDetails do usuário autenticado
     */
    public UserDetails getAuthenticatedUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }

        return null;
    }

    /**
     * Gera o header de Authorization para Basic Auth
     * Útil para testes ou chamadas internas
     *
     * @param username Email do usuário
     * @param password Senha do usuário
     * @return String no formato "Basic base64(username:password)"
     */
    public String generateBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedCredentials;
    }

    /**
     * Decodifica um header Basic Auth
     *
     * @param authHeader Header de Authorization completo
     * @return Array [username, password] ou null se inválido
     */
    public String[] decodeBasicAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null;
        }

        try {
            String base64Credentials = authHeader.substring(6);
            String credentials = new String(
                    Base64.getDecoder().decode(base64Credentials),
                    StandardCharsets.UTF_8
            );

            String[] parts = credentials.split(":", 2);
            if (parts.length == 2) {
                return parts;
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    /**
     * Obtém a Authentication atual
     * @return Authentication object
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Verifica se o usuário autenticado tem uma role específica
     *
     * @param role Nome da role (sem prefixo ROLE_)
     * @return true se o usuário tem a role
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Limpa o contexto de segurança
     * Útil para logout manual ou testes
     */
    public void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }
}
