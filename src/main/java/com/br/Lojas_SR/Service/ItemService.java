package com.br.Lojas_SR.Service;

import com.br.Lojas_SR.Entity.Item;
import com.br.Lojas_SR.Entity.Carrinho;
import com.br.Lojas_SR.Entity.Produto;
import com.br.Lojas_SR.Repository.ItemRepository;
import com.br.Lojas_SR.Repository.CarrinhoRepository;
import com.br.Lojas_SR.Repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CarrinhoService carrinhoService;

    // Adicionar item ao carrinho
    public Item adicionar(Long carrinhoId, Long produtoId, Integer quantidade) {
        // Validações
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Verificar estoque
        if (produto.getEstoque() < quantidade) {
            throw new RuntimeException("Estoque insuficiente");
        }

        // Verificar se produto já está no carrinho
        if (itemRepository.existsByCarrinhoIdAndProdutoId(carrinhoId, produtoId)) {
            // Se já existe, atualizar quantidade
            Item itemExistente = itemRepository.findByCarrinhoIdAndProdutoId(carrinhoId, produtoId)
                    .orElseThrow(() -> new RuntimeException("Erro ao buscar item"));
            return atualizarQuantidade(itemExistente.getId(), itemExistente.getQuantidade() + quantidade);
        }

        // Criar novo item
        Item item = new Item();
        item.setCarrinho(carrinho);
        item.setProduto(produto);
        item.setQuantidade(quantidade);

        // Salvar item
        Item itemSalvo = itemRepository.save(item);

        return itemSalvo;
    }

    // Listar itens do carrinho
    public List<Item> listarPorCarrinho(Long carrinhoId) {
        if (!carrinhoRepository.existsById(carrinhoId)) {
            throw new RuntimeException("Carrinho não encontrado");
        }
        return itemRepository.findByCarrinhoId(carrinhoId);
    }

    // Buscar item por ID
    public Item buscarPorId(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
    }

    // Atualizar quantidade do item
    public Item atualizarQuantidade(Long id, Integer novaQuantidade) {
        Item item = buscarPorId(id);

        // Validar quantidade mínima
        if (novaQuantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }

        // Verificar estoque
        if (item.getProduto().getEstoque() < novaQuantidade) {
            throw new RuntimeException("Estoque insuficiente");
        }

        item.setQuantidade(novaQuantidade);
        Item itemAtualizado = itemRepository.save(item);

        return itemAtualizado;
    }

    // Remover item do carrinho
    public void remover(Long id) {
        Item item = buscarPorId(id);
        Long carrinhoId = item.getCarrinho().getId();

        itemRepository.deleteById(id);
    }

    // Contar itens do carrinho
    public Long contarItens(Long carrinhoId) {
        return itemRepository.countByCarrinhoId(carrinhoId);
    }

    // Calcular subtotal do item
    public BigDecimal calcularSubtotal(Long id) {
        Item item = buscarPorId(id);
        return item.getSubTotal();
    }
}