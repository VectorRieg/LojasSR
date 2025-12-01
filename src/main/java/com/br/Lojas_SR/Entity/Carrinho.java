package com.br.Lojas_SR.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class Carrinho {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL)
    private List<Item> itens;

    public Long getId() { return id; }
    public void setId(Long id) {
        this.id = id;
    }
    public Usuario getUsuario() { return  usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public List<Item> getItens() { return itens; }
    public void setItens(List<Item> itens) { this.itens = itens; }
    public BigDecimal calcularTotal() {
        if (itens == null || itens.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return itens.stream()
                .map(Item::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
