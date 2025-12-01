package com.br.Lojas_SR.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;
    private String nome;
    private String descricao;

    private BigDecimal preco;
    private Integer estoque;

    private String imagemPrincipal; // URL ou caminho

    @ElementCollection
    private List<String> imagensAdicionais; // galeria de fotos

    private String categoria; // Eletrônicos, Roupas, etc
    private String marca;
    private String codigo; // código único do produto

    private Boolean ativo = true;
    private Boolean destaque = false;
    private Boolean emPromocao = false;
    private BigDecimal precoPromocional;
    private LocalDateTime dataCadastro;

    @OneToMany(mappedBy = "produto")
    @JsonIgnore
    private List<Item> itens;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }
    public String getImagemPrincipal() { return imagemPrincipal; }
    public void setImagemPrincipal(String imagemPrincipal) { this.imagemPrincipal = imagemPrincipal; }
    public List<String> getImagensAdicionais() { return imagensAdicionais; }
    public void setImagensAdicionais(List<String> imagensAdicionais) { this.imagensAdicionais = imagensAdicionais; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public Boolean getDestaque() { return destaque; }
    public void setDestaque(Boolean destaque) { this.destaque = destaque; }
    public Boolean getEmPromocao() { return emPromocao; }
    public void setEmPromocao(Boolean emPromocao) { this.emPromocao = emPromocao; }
    public BigDecimal getPrecoPromocional() { return precoPromocional; }
    public void setPrecoPromocional(BigDecimal precoPromocional) { this.precoPromocional = precoPromocional; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
}