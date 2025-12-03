-- ==============
-- PARTE 1 - UP
-- ==============

-- Usuários gerais do sistema (cliente, restaurante, entregador, suporte, etc.)
CREATE TABLE usuarios (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(150)        NOT NULL,
    email           VARCHAR(150)        NOT NULL UNIQUE,
    senha_hash      VARCHAR(255)        NOT NULL,
    telefone        VARCHAR(20),
    tipo_usuario    VARCHAR(20)         NOT NULL, -- 'cliente', 'restaurante', 'entregador', 'suporte'
    foto_perfil     VARCHAR(255),
    criado_em       TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dados específicos de cliente
CREATE TABLE clientes (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id      BIGINT              NOT NULL UNIQUE,
    endereco_padrao TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dados específicos de restaurante
CREATE TABLE restaurantes (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id      BIGINT              NOT NULL UNIQUE,
    nome_fantasia   VARCHAR(150)        NOT NULL,
    cnpj            VARCHAR(18)         NOT NULL UNIQUE,
    endereco        TEXT                NOT NULL,
    dados_bancarios TEXT,
    ativo           BOOLEAN             NOT NULL DEFAULT TRUE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ======================
-- PARTE 2 - UP (H3 e H4)
-- ======================

-- Produtos que os restaurantes oferecem (cardápio)
CREATE TABLE produtos (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurante_id  BIGINT          NOT NULL,
    nome            VARCHAR(150)    NOT NULL,
    descricao       TEXT,
    preco           DECIMAL(10, 2)  NOT NULL,
    categoria       VARCHAR(50),
    imagem          VARCHAR(255),
    ativo           BOOLEAN         NOT NULL DEFAULT TRUE,
    FOREIGN KEY (restaurante_id) REFERENCES restaurantes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Carrinho de compras do cliente
CREATE TABLE carrinhos (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id      BIGINT          NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'aberto',
    criado_em       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Itens do carrinho
CREATE TABLE carrinho_itens (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    carrinho_id     BIGINT          NOT NULL,
    produto_id      BIGINT          NOT NULL,
    quantidade      INT             NOT NULL DEFAULT 1,
    preco_unitario  DECIMAL(10, 2)  NOT NULL,
    FOREIGN KEY (carrinho_id) REFERENCES carrinhos(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id)  REFERENCES produtos(id) ON DELETE CASCADE,
    UNIQUE (carrinho_id, produto_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Pedido gerado a partir do carrinho
CREATE TABLE pedidos (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id          BIGINT          NOT NULL,
    restaurante_id      BIGINT          NOT NULL,
    carrinho_id         BIGINT,
    valor_total         DECIMAL(10, 2)  NOT NULL,
    status              VARCHAR(20)     NOT NULL,
    endereco_entrega    TEXT            NOT NULL,
    criado_em           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id)     REFERENCES clientes(id) ON DELETE CASCADE,
    FOREIGN KEY (restaurante_id) REFERENCES restaurantes(id) ON DELETE CASCADE,
    FOREIGN KEY (carrinho_id)    REFERENCES carrinhos(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Itens do pedido (snapshot do carrinho no momento da compra)
CREATE TABLE pedido_itens (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id       BIGINT          NOT NULL,
    produto_id      BIGINT          NOT NULL,
    quantidade      INT             NOT NULL,
    preco_unitario  DECIMAL(10, 2)  NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produtos(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===========================
-- PARTE 3 - UP (H5 e H6)
-- ===========================

-- Dados específicos do entregador
CREATE TABLE entregadores (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id      BIGINT          NOT NULL UNIQUE,
    veiculo_tipo    VARCHAR(50),
    documento       VARCHAR(30),
    ativo           BOOLEAN         NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Entrega associada a um pedido
CREATE TABLE entregas (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id           BIGINT          NOT NULL UNIQUE,
    entregador_id       BIGINT,
    status              VARCHAR(20)     NOT NULL,
    rota_sugerida       TEXT,
    tempo_estimado_min  INT,
    distancia_km        DECIMAL(6, 2),
    criado_em           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id)     REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (entregador_id) REFERENCES entregadores(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Registro de aceitação/recusa de ofertas de entrega
CREATE TABLE entrega_respostas (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    entrega_id      BIGINT          NOT NULL,
    entregador_id   BIGINT          NOT NULL,
    resposta        VARCHAR(10)     NOT NULL,
    criado_em       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (entrega_id)    REFERENCES entregas(id) ON DELETE CASCADE,
    FOREIGN KEY (entregador_id) REFERENCES entregadores(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===========================
-- PARTE 4 - UP (H7 - Avaliação)
-- ===========================

-- Avaliação de restaurantes
CREATE TABLE avaliacoes_restaurantes (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id       BIGINT          NOT NULL,
    restaurante_id   BIGINT          NOT NULL,
    pedido_id        BIGINT          NOT NULL,
    nota             INT             NOT NULL CHECK (nota >= 1 AND nota <= 5),
    comentario       TEXT,
    criado_em        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id)     REFERENCES clientes(id) ON DELETE CASCADE,
    FOREIGN KEY (restaurante_id) REFERENCES restaurantes(id) ON DELETE CASCADE,
    FOREIGN KEY (pedido_id)      REFERENCES pedidos(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX ux_avaliacao_restaurante_unica
ON avaliacoes_restaurantes (cliente_id, restaurante_id, pedido_id);

-- Avaliação de entregadores
CREATE TABLE avaliacoes_entregadores (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id       BIGINT          NOT NULL,
    entregador_id    BIGINT          NOT NULL,
    pedido_id        BIGINT          NOT NULL,
    nota             INT             NOT NULL CHECK (nota >= 1 AND nota <= 5),
    comentario       TEXT,
    criado_em        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id)      REFERENCES clientes(id) ON DELETE CASCADE,
    FOREIGN KEY (entregador_id)   REFERENCES entregadores(id) ON DELETE CASCADE,
    FOREIGN KEY (pedido_id)       REFERENCES pedidos(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX ux_avaliacao_entregador_unica
ON avaliacoes_entregadores (cliente_id, entregador_id, pedido_id);

-- ===========================
-- PARTE 4 - UP (H8 - Promoções)
-- ===========================

-- Promoções cadastradas na plataforma
CREATE TABLE promocoes (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurante_id      BIGINT,
    titulo              VARCHAR(150)  NOT NULL,
    descricao           TEXT,
    tipo_desconto       VARCHAR(20)   NOT NULL,
    valor_desconto      DECIMAL(10, 2),
    data_inicio         TIMESTAMP     NOT NULL,
    data_fim            TIMESTAMP     NOT NULL,
    ativo               BOOLEAN       NOT NULL DEFAULT TRUE,
    criado_em           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurante_id) REFERENCES restaurantes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Promoções direcionadas ou resgatadas pelos clientes
CREATE TABLE promocoes_clientes (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    promocao_id         BIGINT          NOT NULL,
    cliente_id          BIGINT          NOT NULL,
    resgatada           BOOLEAN         NOT NULL DEFAULT FALSE,
    resgatada_em        TIMESTAMP,
    FOREIGN KEY (promocao_id) REFERENCES promocoes(id) ON DELETE CASCADE,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX ux_promocao_cliente_unica
ON promocoes_clientes (promocao_id, cliente_id);

-- ===========================
-- PARTE 5 - UP (H9 - Usuário Premium)
-- ===========================

-- Planos premium disponíveis
CREATE TABLE planos_premium (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(100)    NOT NULL,
    descricao       TEXT,
    valor_mensal    DECIMAL(10, 2)  NOT NULL,
    duracao_dias    INT             NOT NULL DEFAULT 30,
    ativo           BOOLEAN         NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Assinaturas premium dos clientes
CREATE TABLE assinaturas_premium (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id              BIGINT          NOT NULL,
    plano_id                BIGINT          NOT NULL,
    status                  VARCHAR(20)     NOT NULL,
    data_inicio             TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_fim                TIMESTAMP,
    renovacao_automatica    BOOLEAN         NOT NULL DEFAULT TRUE,
    metodo_pagamento        VARCHAR(50),
    referencia_pagamento    VARCHAR(100),
    criado_em               TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE,
    FOREIGN KEY (plano_id)   REFERENCES planos_premium(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===========================
-- PARTE 5 - UP (H10 - Suporte Técnico)
-- ===========================

-- Atendimentos de suporte
CREATE TABLE suporte_atendimentos (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id      BIGINT          NOT NULL,
    assunto         VARCHAR(150),
    status          VARCHAR(20)     NOT NULL,
    criado_em       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    encerrado_em    TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Mensagens trocadas no chat de suporte
CREATE TABLE suporte_mensagens (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    atendimento_id          BIGINT          NOT NULL,
    remetente_tipo          VARCHAR(20)     NOT NULL,
    remetente_usuario_id    BIGINT,
    mensagem                TEXT            NOT NULL,
    enviado_em              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (atendimento_id)      REFERENCES suporte_atendimentos(id) ON DELETE CASCADE,
    FOREIGN KEY (remetente_usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===========================
-- DADOS INICIAIS (SEED DATA)
-- ===========================

-- Inserir plano premium padrão
INSERT INTO planos_premium (nome, descricao, valor_mensal, duracao_dias, ativo) 
VALUES (
    'Foodly Premium',
    'Entregas grátis ilimitadas e descontos exclusivos em restaurantes parceiros',
    29.90,
    30,
    TRUE
);