-- Script para criar todas as tabelas necessárias no PostgreSQL
-- Execute este script no seu banco de dados antes de rodar a aplicação

-- Tabela Usuario
CREATE TABLE IF NOT EXISTS usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    cep VARCHAR(10),
    rua VARCHAR(255),
    numero VARCHAR(20),
    complemento VARCHAR(255),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    ativo BOOLEAN DEFAULT true
);

-- Tabela Produto
CREATE TABLE IF NOT EXISTS produto (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10,2) NOT NULL,
    estoque INTEGER DEFAULT 0,
    imagem_principal VARCHAR(500),
    categoria VARCHAR(100),
    marca VARCHAR(100),
    codigo VARCHAR(50) UNIQUE,
    ativo BOOLEAN DEFAULT true,
    destaque BOOLEAN DEFAULT false,
    em_promocao BOOLEAN DEFAULT false,
    preco_promocional DECIMAL(10,2),
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela Carrinho
CREATE TABLE IF NOT EXISTS carrinho (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT UNIQUE NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Tabela Item (itens do carrinho)
CREATE TABLE IF NOT EXISTS item (
    id BIGSERIAL PRIMARY KEY,
    carrinho_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL DEFAULT 1,
    preco_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (carrinho_id) REFERENCES carrinho(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE CASCADE
);

-- Tabela Pedido
CREATE TABLE IF NOT EXISTS pedido (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDENTE',
    valor_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Tabela Endereco
CREATE TABLE IF NOT EXISTS endereco (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    cep VARCHAR(10),
    rua VARCHAR(255),
    numero VARCHAR(20),
    complemento VARCHAR(255),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    principal BOOLEAN DEFAULT false,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Tabela Pagamento
CREATE TABLE IF NOT EXISTS pagamento (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT UNIQUE NOT NULL,
    metodo VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDENTE',
    valor DECIMAL(10,2) NOT NULL,
    data_pagamento TIMESTAMP,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE
);

-- Tabela para imagens adicionais do produto
CREATE TABLE IF NOT EXISTS produto_imagens_adicionais (
    produto_id BIGINT NOT NULL,
    imagens_adicionais VARCHAR(500),
    FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE CASCADE
);

-- Índices para melhorar performance
CREATE INDEX IF NOT EXISTS idx_usuario_email ON usuario(email);
CREATE INDEX IF NOT EXISTS idx_produto_categoria ON produto(categoria);
CREATE INDEX IF NOT EXISTS idx_produto_ativo ON produto(ativo);
CREATE INDEX IF NOT EXISTS idx_carrinho_usuario ON carrinho(usuario_id);
CREATE INDEX IF NOT EXISTS idx_item_carrinho ON item(carrinho_id);
CREATE INDEX IF NOT EXISTS idx_pedido_usuario ON pedido(usuario_id);

COMMIT;
