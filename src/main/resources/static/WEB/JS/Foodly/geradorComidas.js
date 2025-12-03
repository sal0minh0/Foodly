// Dados dos cardápios (reutilizando do restaurantes.js)
const cardapios = {
  "Burger Prime": [
    {
      nome: "Smash Clássico",
      preco: "R$ 18,90",
      img: "https://images.pexels.com/photos/1639557/pexels-photo-1639557.jpeg",
      descricao: "Hambúrguer artesanal 150g, queijo, alface, tomate",
      categoria: "hamburguer",
    },
    {
      nome: "Duplo Bacon",
      preco: "R$ 24,90",
      img: "https://images.pexels.com/photos/2983098/pexels-photo-2983098.jpeg",
      descricao: "Dois hambúrgueres, bacon, queijo cheddar",
      categoria: "hamburguer",
    },
    {
      nome: "Fritas Especiais",
      preco: "R$ 12,90",
      img: "https://images.pexels.com/photos/1893556/pexels-photo-1893556.jpeg",
      descricao: "Batatas fritas artesanais com tempero especial",
      categoria: "hamburguer",
    },
  ],
  "Nonna Bella": [
    {
      nome: "Pizza Margherita",
      preco: "R$ 32,90",
      img: "https://images.pexels.com/photos/262978/pexels-photo-262978.jpeg",
      descricao: "Molho de tomate, mussarela, manjericão",
      categoria: "pizza",
    },
    {
      nome: "Pizza Pepperoni",
      preco: "R$ 38,90",
      img: "https://images.pexels.com/photos/315755/pexels-photo-315755.jpeg",
      descricao: "Molho, mussarela, pepperoni, orégano",
      categoria: "pizza",
    },
    {
      nome: "Lasanha Bolonhesa",
      preco: "R$ 28,90",
      img: "https://images.pexels.com/photos/4079520/pexels-photo-4079520.jpeg",
      descricao: "Massa, molho bolonhesa, queijo gratinado",
      categoria: "pizza",
    },
  ],
  "Green Life": [
    {
      nome: "Bowl Fitness",
      preco: "R$ 22,90",
      img: "https://images.pexels.com/photos/1640770/pexels-photo-1640770.jpeg",
      descricao: "Quinoa, frango grelhado, abacate, vegetais",
      categoria: "saudavel",
    },
    {
      nome: "Salada Caesar",
      preco: "R$ 18,90",
      img: "https://images.pexels.com/photos/2097090/pexels-photo-2097090.jpeg",
      descricao: "Alface romana, croutons, parmesão, molho caesar",
      categoria: "saudavel",
    },
    {
      nome: "Smoothie Detox",
      preco: "R$ 12,90",
      img: "https://images.pexels.com/photos/616836/pexels-photo-616836.jpeg",
      descricao: "Couve, maçã, gengibre, limão",
      categoria: "saudavel",
    },
  ],
  "Tokyo Sushi": [
    {
      nome: "Combinado Especial",
      preco: "R$ 45,90",
      img: "https://images.pexels.com/photos/2098085/pexels-photo-2098085.jpeg",
      descricao: "12 peças variadas de sushi e sashimi",
      categoria: "sushi",
    },
    {
      nome: "Temaki Salmão",
      preco: "R$ 18,90",
      img: "https://images.pexels.com/photos/357756/pexels-photo-357756.jpeg",
      descricao: "Temaki de salmão fresco com cream cheese",
      categoria: "sushi",
    },
    {
      nome: "Hot Roll",
      preco: "R$ 24,90",
      img: "https://images.pexels.com/photos/2098085/pexels-photo-2098085.jpeg",
      descricao: "Uramaki empanado e frito, salmão, cream cheese",
      categoria: "sushi",
    },
  ],
  "Doce Encanto": [
    {
      nome: "Brownie Premium",
      preco: "R$ 14,90",
      img: "https://images.pexels.com/photos/4109992/pexels-photo-4109992.jpeg",
      descricao: "Brownie artesanal com calda e sorvete",
      categoria: "sobremesa",
    },
    {
      nome: "Cheesecake",
      preco: "R$ 16,90",
      img: "https://images.pexels.com/photos/140831/pexels-photo-140831.jpeg",
      descricao: "Cheesecake cremoso com frutas vermelhas",
      categoria: "sobremesa",
    },
    {
      nome: "Torta Holandesa",
      preco: "R$ 18,90",
      img: "https://images.pexels.com/photos/1126359/pexels-photo-1126359.jpeg",
      descricao: "Torta com creme e cobertura de chocolate",
      categoria: "sobremesa",
    },
  ],
};

// Variáveis globais
let historicoSugestoes = JSON.parse(
  localStorage.getItem("historicoSugestoes") || "[]"
);
let itemAtualSelecionado = null;

// Sistema de carrinho (simplificado)
let carrinho = JSON.parse(localStorage.getItem("carrinho") || "[]");

// Função para obter todos os itens do cardápio
function obterTodosItens() {
  const todosItens = [];
  Object.entries(cardapios).forEach(([restaurante, itens]) => {
    itens.forEach((item) => {
      todosItens.push({
        ...item,
        restaurante: restaurante,
      });
    });
  });
  return todosItens;
}

// Função para gerar item aleatório
function gerarItemAleatorio() {
  const todosItens = obterTodosItens();

  if (todosItens.length === 0) {
    alert("Nenhum item encontrado!");
    return null;
  }

  const indiceAleatorio = Math.floor(Math.random() * todosItens.length);
  const itemSelecionado = todosItens[indiceAleatorio];

  // Adicionar ao histórico
  adicionarAoHistorico(itemSelecionado);

  return itemSelecionado;
}

// Função para exibir resultado
function exibirResultado(item) {
  const generatorResult = document.getElementById("generatorResult");
  const emptyState = document.getElementById("emptyState");
  const resultCard = document.getElementById("resultCard");
  const addToCartBtn = document.getElementById("addToCartBtn");

  itemAtualSelecionado = item;

  // Esconder estado vazio e mostrar resultado
  emptyState.style.display = "none";
  generatorResult.style.display = "block";

  // Construir card do resultado
  resultCard.innerHTML = `
    <img src="${item.img}" alt="${item.nome}" class="result-image">
    <div class="result-info">
      <h3 class="result-title">${item.nome}</h3>
      <p class="result-restaurant"><img src="../../assets/location.svg" alt="Location"> ${item.restaurante}</p>
      <p class="result-description">${item.descricao}</p>
      <div class="result-price">${item.preco}</div>
    </div>
  `;

  // Mostrar botão de adicionar ao carrinho
  addToCartBtn.style.display = "flex";

  // Scroll suave para o resultado
  generatorResult.scrollIntoView({ behavior: "smooth" });
}

// Função para adicionar ao histórico
function adicionarAoHistorico(item) {
  // Evitar duplicatas recentes
  const itemExiste = historicoSugestoes.findIndex(
    (histItem) =>
      histItem.nome === item.nome && histItem.restaurante === item.restaurante
  );

  if (itemExiste !== -1) {
    historicoSugestoes.splice(itemExiste, 1);
  }

  // Adicionar no início
  historicoSugestoes.unshift({
    ...item,
    timestamp: Date.now(),
  });

  // Manter apenas os últimos 10 itens
  if (historicoSugestoes.length > 10) {
    historicoSugestoes = historicoSugestoes.slice(0, 10);
  }

  // Salvar no localStorage
  localStorage.setItem(
    "historicoSugestoes",
    JSON.stringify(historicoSugestoes)
  );

  // Atualizar exibição do histórico
  atualizarHistorico();
}

// Função para atualizar histórico na interface
function atualizarHistorico() {
  const historySection = document.getElementById("historySection");
  const historyList = document.getElementById("historyList");

  if (historicoSugestoes.length === 0) {
    historySection.style.display = "none";
    return;
  }

  historySection.style.display = "block";

  historyList.innerHTML = historicoSugestoes
    .map(
      (item) => `
    <div class="history-item" onclick="exibirResultado(${JSON.stringify(
      item
    ).replace(/"/g, "&quot;")})">
      <img src="${item.img}" alt="${item.nome}" class="history-item-image">
      <div class="history-item-title">${item.nome}</div>
      <div class="history-item-restaurant">${item.restaurante}</div>
      <div class="history-item-price">${item.preco}</div>
    </div>
  `
    )
    .join("");
}

// Função para limpar histórico
function limparHistorico() {
  if (confirm("Tem certeza que deseja limpar o histórico de sugestões?")) {
    historicoSugestoes = [];
    localStorage.setItem(
      "historicoSugestoes",
      JSON.stringify(historicoSugestoes)
    );
    document.getElementById("historySection").style.display = "none";
  }
}

// Função para adicionar ao carrinho
function adicionarAoCarrinho(item, restaurante) {
  if (!item || !restaurante) return;

  // Verificar se item já existe no carrinho
  const itemExistente = carrinho.find(
    (cartItem) =>
      cartItem.nome === item.nome && cartItem.restaurante === restaurante
  );

  if (itemExistente) {
    itemExistente.quantidade++;
  } else {
    carrinho.push({
      nome: item.nome,
      preco: item.preco,
      precoFinal: item.preco, // Simplificado, sem desconto premium
      img: item.img,
      restaurante: restaurante,
      quantidade: 1,
    });
  }

  // Salvar carrinho
  localStorage.setItem("carrinho", JSON.stringify(carrinho));

  // Feedback visual
  mostrarNotificacao(`${item.nome} adicionado ao carrinho!`);

  // Atualizar badge do carrinho (se existir)
  atualizarCarrinho();
}

// Função para mostrar notificação
function mostrarNotificacao(mensagem) {
  const notificacao = document.createElement("div");
  notificacao.style.cssText = `
    position: fixed;
    top: 80px;
    right: 20px;
    background: #10b981;
    color: white;
    padding: 12px 16px;
    border-radius: 8px;
    font-size: 14px;
    z-index: 3000;
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
    transform: translateX(100%);
    transition: transform 0.3s ease;
  `;
  notificacao.innerHTML = mensagem;

  document.body.appendChild(notificacao);

  // Animar entrada
  setTimeout(() => {
    notificacao.style.transform = "translateX(0)";
  }, 100);

  // Remover após 3 segundos
  setTimeout(() => {
    notificacao.style.transform = "translateX(100%)";
    setTimeout(() => notificacao.remove(), 300);
  }, 3000);
}

// Função para atualizar carrinho (simplificada)
function atualizarCarrinho() {
  const cartBadge = document.getElementById("cart-badge");
  if (cartBadge) {
    const totalItens = carrinho.reduce((sum, item) => sum + item.quantidade, 0);
    if (totalItens > 0) {
      cartBadge.textContent = totalItens;
      cartBadge.style.display = "flex";
    } else {
      cartBadge.style.display = "none";
    }
  }
}

// Event Listeners
document.addEventListener("DOMContentLoaded", () => {
  // Inicializar histórico
  atualizarHistorico();
  atualizarCarrinho();

  // Botão gerar
  document.getElementById("generateBtn").addEventListener("click", () => {
    const item = gerarItemAleatorio();
    if (item) {
      exibirResultado(item);
    }
  });

  // Botão nova sugestão
  document.getElementById("newSuggestionBtn").addEventListener("click", () => {
    const item = gerarItemAleatorio();
    if (item) {
      exibirResultado(item);
    }
  });

  // Botão adicionar ao carrinho
  document.getElementById("addToCartBtn").addEventListener("click", () => {
    if (itemAtualSelecionado) {
      adicionarAoCarrinho(
        itemAtualSelecionado,
        itemAtualSelecionado.restaurante
      );
    }
  });

  // Botão limpar histórico
  document
    .getElementById("clearHistoryBtn")
    .addEventListener("click", limparHistorico);
});
