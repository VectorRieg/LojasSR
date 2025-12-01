package com.br.Lojas_SR.Config;

import com.br.Lojas_SR.Entity.Produto;
import com.br.Lojas_SR.Entity.Usuario;
import com.br.Lojas_SR.Repository.AcessoRepository;
import com.br.Lojas_SR.Repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private AcessoRepository acessoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se já existem dados
        if (produtoRepository.count() > 0) {
            System.out.println("Base de dados já contém produtos. Pulando inicialização...");
            return;
        }

        System.out.println("Inicializando base de dados com dados de exemplo...");

        // Criar usuário de teste
        if (!acessoRepository.existsByEmail("teste@lojassr.com")) {
            Usuario usuarioTeste = new Usuario();
            usuarioTeste.setNome("Usuário Teste");
            usuarioTeste.setEmail("teste@lojassr.com");
            usuarioTeste.setSenha(passwordEncoder.encode("senha123"));
            usuarioTeste.setCpf("12345678900");
            usuarioTeste.setTelefone("11999999999");
            usuarioTeste.setCep("01310-100");
            usuarioTeste.setRua("Avenida Paulista");
            usuarioTeste.setNumero("1000");
            usuarioTeste.setBairro("Bela Vista");
            usuarioTeste.setCidade("São Paulo");
            usuarioTeste.setEstado("SP");
            usuarioTeste.setAtivo(true);
            acessoRepository.save(usuarioTeste);
            System.out.println("✅ Usuário teste criado: teste@lojassr.com / senha123");
        }

        // Criar produtos de exemplo
        criarProduto("Notebook Dell Inspiron 15",
            "Notebook com processador Intel Core i5, 8GB RAM, SSD 256GB",
            new BigDecimal("3499.00"), 15, "Eletrônicos", "Dell",
            "https://via.placeholder.com/400x300/0066cc/ffffff?text=Notebook+Dell", true, false);

        criarProduto("Mouse Gamer Logitech G203",
            "Mouse gamer com iluminação RGB, 8000 DPI",
            new BigDecimal("149.90"), 50, "Eletrônicos", "Logitech",
            "https://via.placeholder.com/400x300/333333/ffffff?text=Mouse+Gamer", false, true);

        criarProduto("Teclado Mecânico Redragon",
            "Teclado mecânico RGB, switches azuis",
            new BigDecimal("299.90"), new BigDecimal("249.90"), 30, "Eletrônicos", "Redragon",
            "https://via.placeholder.com/400x300/ff0000/ffffff?text=Teclado+Mecanico", true, true);

        criarProduto("Headset HyperX Cloud Stinger",
            "Headset gamer com som surround",
            new BigDecimal("279.90"), 25, "Eletrônicos", "HyperX",
            "https://via.placeholder.com/400x300/000000/ffffff?text=Headset+HyperX", false, false);

        criarProduto("Monitor LG 24\" Full HD",
            "Monitor LED 24 polegadas, Full HD 1080p, 75Hz",
            new BigDecimal("799.00"), 20, "Eletrônicos", "LG",
            "https://via.placeholder.com/400x300/0066cc/ffffff?text=Monitor+LG", true, false);

        criarProduto("Webcam Logitech C920",
            "Webcam Full HD 1080p com microfone",
            new BigDecimal("449.90"), new BigDecimal("399.90"), 18, "Eletrônicos", "Logitech",
            "https://via.placeholder.com/400x300/333333/ffffff?text=Webcam+C920", false, true);

        criarProduto("SSD Kingston 480GB",
            "SSD SATA 2.5\" com velocidade de leitura de 500MB/s",
            new BigDecimal("289.90"), 40, "Eletrônicos", "Kingston",
            "https://via.placeholder.com/400x300/ff6600/ffffff?text=SSD+Kingston", false, false);

        criarProduto("Memória RAM Corsair 16GB DDR4",
            "Memória RAM 16GB DDR4 3200MHz",
            new BigDecimal("399.00"), 35, "Eletrônicos", "Corsair",
            "https://via.placeholder.com/400x300/000000/ffffff?text=RAM+Corsair", false, false);

        criarProduto("Placa de Vídeo RTX 3060",
            "Placa de vídeo NVIDIA GeForce RTX 3060 12GB",
            new BigDecimal("2499.00"), new BigDecimal("2199.00"), 8, "Eletrônicos", "NVIDIA",
            "https://via.placeholder.com/400x300/76b900/ffffff?text=RTX+3060", true, true);

        criarProduto("Cadeira Gamer DT3 Sports",
            "Cadeira gamer ergonômica com apoio lombar",
            new BigDecimal("1299.90"), 12, "Móveis", "DT3 Sports",
            "https://via.placeholder.com/400x300/ff0000/ffffff?text=Cadeira+Gamer", false, false);

        System.out.println("✅ 10 produtos de exemplo criados!");
        System.out.println("\n=== DADOS DE TESTE ===");
        System.out.println("Email: teste@lojassr.com");
        System.out.println("Senha: senha123");
        System.out.println("=======================\n");
    }

    private void criarProduto(String nome, String descricao, BigDecimal preco,
                             int estoque, String categoria, String marca,
                             String imagem, boolean destaque, boolean emPromocao) {
        criarProduto(nome, descricao, preco, null, estoque, categoria, marca, imagem, destaque, emPromocao);
    }

    private void criarProduto(String nome, String descricao, BigDecimal preco,
                             BigDecimal precoPromocional, int estoque, String categoria,
                             String marca, String imagem, boolean destaque, boolean emPromocao) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setPrecoPromocional(precoPromocional);
        produto.setEstoque(estoque);
        produto.setCategoria(categoria);
        produto.setMarca(marca);
        produto.setImagemPrincipal(imagem);
        produto.setImagensAdicionais(Arrays.asList(imagem, imagem, imagem));
        produto.setCodigo("PROD-" + System.currentTimeMillis());
        produto.setAtivo(true);
        produto.setDestaque(destaque);
        produto.setEmPromocao(emPromocao);
        produto.setDataCadastro(LocalDateTime.now());
        produtoRepository.save(produto);
    }
}
