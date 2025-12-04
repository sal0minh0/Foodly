const API_URL = CONFIG.API_URL;
let usuarioAtual = null;
let isPremium = false;

// Verificar se usuário está logado (sem restrição premium)
async function verificarUsuarioLogado() {
  try {
    const usuarioJson = localStorage.getItem("usuario");

    if (!usuarioJson) {
      alert("⚠️ Acesso Restrito!\n\nFaça login para continuar.");
      window.location.href = "../../html/escolhaPerfil.html";
      return;
    }

    usuarioAtual = JSON.parse(usuarioJson);

    // Verificar se é cliente
    if (usuarioAtual.tipoUsuario !== "cliente" || !usuarioAtual.clienteId) {
      alert("⚠️ Acesso Restrito!\n\nEsta página é exclusiva para clientes.");
      window.location.href = "../../html/escolhaPerfil.html";
      return;
    }

    console.log("👤 Usuário logado:", usuarioAtual);

    // SEMPRE VERIFICAR NO SERVIDOR SE O CLIENTE TEM PREMIUM
    console.log("🔍 Verificando status premium no servidor...");
    try {
      await verificarStatusPremium();
    } catch (error) {
      console.log("❌ Verificação premium falhou - assumindo usuário básico");
      isPremium = false;
    }

    // APLICAR BENEFÍCIOS PREMIUM SEMPRE QUE isPremium for true
    console.log("🎯 Status Premium final:", isPremium);

    if (isPremium) {
      console.log("✅ Aplicando benefícios premium...");
      mostrarBadgePremium();

      // Aguardar um pouco para garantir que os cards estejam carregados
      setTimeout(() => {
        aplicarDescontosPremium();
      }, 500);
    } else {
      console.log("ℹ️ Usuário básico - interface padrão");
    }
  } catch (error) {
    console.error("Erro ao verificar usuário:", error);
    alert("Erro ao verificar acesso. Redirecionando...");
    window.location.href = "../../html/Foodly/menuPrincipal.html";
  }
}

// Verificar status premium (modificado para sempre tentar)
async function verificarStatusPremium() {
  // Verificar se tem clienteId válido
  if (!usuarioAtual || !usuarioAtual.clienteId || usuarioAtual.clienteId <= 0) {
    console.log("❌ Cliente ID inválido:", usuarioAtual?.clienteId);
    isPremium = false;
    return false;
  }

  try {
    console.log(
      "🌐 Fazendo requisição para:",
      `${API_URL}/premium/cliente/${usuarioAtual.clienteId}`
    );

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 5000); // 5 segundos timeout

    const response = await fetch(
      `${API_URL}/premium/cliente/${usuarioAtual.clienteId}`,
      {
        method: "GET",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        signal: controller.signal,
      }
    );

    clearTimeout(timeoutId);

    console.log("📡 Resposta do servidor:", response.status);

    if (response.ok) {
      const assinatura = await response.json();
      console.log("📦 Dados recebidos:", assinatura);

      if (assinatura && assinatura.status === "ativa") {
        console.log("🎉 Usuário PREMIUM confirmado pelo servidor!");
        isPremium = true;
        return true;
      } else {
        console.log("📋 Assinatura encontrada mas não ativa:", assinatura);
        isPremium = false;
        return false;
      }
    } else if (response.status === 404) {
      console.log("📭 Nenhuma assinatura premium encontrada (404)");
      isPremium = false;
      return false;
    } else {
      console.log("⚠️ Resposta inesperada do servidor:", response.status);
      // Tentar ler o corpo da resposta para debug
      try {
        const errorText = await response.text();
        console.log("📄 Corpo da resposta:", errorText);
      } catch (e) {
        console.log("❌ Erro ao ler corpo da resposta");
      }
      isPremium = false;
      return false;
    }
  } catch (error) {
    if (error.name === "AbortError") {
      console.log("⏰ Timeout na verificação premium");
    } else {
      console.error("❌ Erro na verificação premium:", error);
    }
    isPremium = false;
    return false;
  }
}

// Mostrar badge premium na navbar
function mostrarBadgePremium() {
  const navbar = document.querySelector(".navbar-right");
  if (navbar && !document.querySelector(".premium-indicator")) {
    const premiumIndicator = document.createElement("div");
    premiumIndicator.className = "premium-indicator";
    premiumIndicator.innerHTML = "PREMIUM";
    navbar.insertBefore(premiumIndicator, navbar.firstChild);
  }
}

// Aplicar descontos premium nos preços
function aplicarDescontosPremium() {
  console.log("🎯 Aplicando descontos premium nos cards...");
  const restaurantCards = document.querySelectorAll(".restaurant-card");

  restaurantCards.forEach((card, index) => {
    console.log(
      `Processando card ${index + 1}:`,
      card.querySelector("h2")?.textContent
    );

    // Adicionar badge premium no card
    if (!card.querySelector(".premium-discount-badge")) {
      const badge = document.createElement("div");
      badge.className = "premium-discount-badge";
      badge.innerHTML = "DESCONTO PREMIUM";
      card.appendChild(badge);
      console.log("✅ Badge premium adicionado ao card", index + 1);
    }

    // Modificar taxa de entrega
    const feeElement = card.querySelector(".restaurant-fee");
    if (feeElement && !feeElement.innerHTML.includes("PREMIUM")) {
      const originalText = feeElement.innerHTML;

      if (originalText.includes("R$")) {
        feeElement.innerHTML = `<span class="original-price">${originalText}</span><br><strong class="premium-price">ENTREGA GRÁTIS PREMIUM</strong>`;
        console.log(
          "✅ Taxa de entrega atualizada para grátis premium",
          index + 1
        );
      } else if (originalText.includes("Grátis")) {
        feeElement.innerHTML = `${originalText} <span class="premium-bonus">+ Desconto 20% no pedido ⭐</span>`;
        console.log(
          "✅ Bonus premium adicionado à entrega gratuita",
          index + 1
        );
      }
    }

    // Adicionar indicador de preços reduzidos
    const metaDiv = card.querySelector(".restaurant-meta");
    if (metaDiv && !metaDiv.querySelector(".premium-price-indicator")) {
      const priceIndicator = document.createElement("span");
      priceIndicator.className = "premium-price-indicator";
      priceIndicator.innerHTML = "Preço à -20%";
      metaDiv.appendChild(priceIndicator);
      console.log("✅ Indicador de preços premium adicionado", index + 1);
    }
  });

  // Adicionar banner premium no topo
  adicionarBannerPremium();
  console.log("🎉 Todos os benefícios premium aplicados!");
}

// Adicionar banner informativo premium
function adicionarBannerPremium() {
  // Verificar se já existe um banner
  if (document.querySelector(".premium-toast")) {
    console.log("Banner premium já existe");
    return;
  }

  console.log("🎯 Criando banner premium...");

  const banner = document.createElement("div");
  banner.className = "premium-banner premium-toast";
  banner.innerHTML = `
    <div class="premium-banner-content">
      <span class="premium-icon-grande">⭐</span>
      <div class="premium-text">
        <h3>Bem-vindo, usuário Premium!</h3>
        <p>Aproveite preços especiais, entrega grátis e descontos exclusivos em todos os restaurantes.</p>
      </div>
    </div>
  `;

  // Adicionar no topo da página
  document.body.appendChild(banner);
  console.log("✅ Banner premium criado");

  // Animar entrada
  setTimeout(() => {
    banner.classList.add("show");
    console.log("✅ Banner premium animado");
  }, 100);

  // Auto fechar após 8 segundos (aumentei o tempo)
  setTimeout(() => {
    fecharBannerPremium();
  }, 8000);
}

// Fechar banner premium
function fecharBannerPremium() {
  const banner = document.querySelector(".premium-toast");
  if (banner) {
    banner.classList.remove("show");
    banner.classList.add("hide");

    setTimeout(() => {
      banner.remove();
    }, 300);
  }
}

// Dados de exemplo dos cardápios
const cardapios = {
  "Burger Prime": [
    {
      nome: "Smash Clássico",
      preco: "R$ 18,90",
      img: "https://images.pexels.com/photos/1639557/pexels-photo-1639557.jpeg",
      descricao: "Hambúrguer artesanal 150g, queijo, alface, tomate",
    },
    {
      nome: "Duplo Bacon",
      preco: "R$ 24,90",
      img: "https://images.pexels.com/photos/2983098/pexels-photo-2983098.jpeg",
      descricao: "Dois hambúrgueres, bacon, queijo cheddar",
    },
    {
      nome: "Fritas Especiais",
      preco: "R$ 12,90",
      img: "https://images.pexels.com/photos/1893556/pexels-photo-1893556.jpeg",
      descricao: "Batatas fritas artesanais com tempero especial",
    },
    {
      nome: "Milk Shake",
      preco: "R$ 8,90",
      img: "http://blog.atacadao.com.br/wp-content/uploads/2024/01/Milk-shake-caseiro-aprenda-tres-receitas-deliciosas-Foto-de-tres-copos-de-milk-shakes-de-diferentes-sabores-Atacadao.jpg",
      descricao: "Milk shake cremoso sabores variados",
    },
  ],
  "Nonna Bella": [
    {
      nome: "Pizza Margherita",
      preco: "R$ 32,90",
      img: "https://images.pexels.com/photos/262978/pexels-photo-262978.jpeg",
      descricao: "Molho de tomate, mussarela, manjericão",
    },
    {
      nome: "Pizza Pepperoni",
      preco: "R$ 38,90",
      img: "https://images.pexels.com/photos/315755/pexels-photo-315755.jpeg",
      descricao: "Molho, mussarela, pepperoni, orégano",
    },
    {
      nome: "Lasanha Bolonhesa",
      preco: "R$ 28,90",
      img: "https://images.pexels.com/photos/4079520/pexels-photo-4079520.jpeg",
      descricao: "Massa, molho bolonhesa, queijo gratinado",
    },
    {
      nome: "Tiramisu",
      preco: "R$ 14,90",
      img: "https://images.pexels.com/photos/6880219/pexels-photo-6880219.jpeg",
      descricao: "Sobremesa italiana tradicional",
    },
  ],
  "Green Life": [
    {
      nome: "Bowl Fitness",
      preco: "R$ 22,90",
      img: "https://images.pexels.com/photos/1640770/pexels-photo-1640770.jpeg",
      descricao: "Quinoa, frango grelhado, abacate, vegetais",
    },
    {
      nome: "Salada Caesar",
      preco: "R$ 18,90",
      img: "https://images.pexels.com/photos/2097090/pexels-photo-2097090.jpeg",
      descricao: "Alface romana, croutons, parmesão, molho caesar",
    },
    {
      nome: "Smoothie Detox",
      preco: "R$ 12,90",
      img: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcToW_iSrpSO8LiVaqpzxkw1sTlHIhhkRaCVEQ&s",
      descricao: "Couve, maçã, gengibre, limão",
    },
    {
      nome: "Wrap Integral",
      preco: "R$ 16,90",
      img: "https://images.pexels.com/photos/4958792/pexels-photo-4958792.jpeg",
      descricao: "Tortilha integral, frango, vegetais",
    },
  ],
  "Tokyo Sushi": [
    {
      nome: "Combinado Especial",
      preco: "R$ 45,90",
      img: "https://images.pexels.com/photos/2098085/pexels-photo-2098085.jpeg",
      descricao: "12 peças variadas de sushi e sashimi",
    },
    {
      nome: "Temaki Salmão",
      preco: "R$ 18,90",
      img: "https://images.pexels.com/photos/357756/pexels-photo-357756.jpeg",
      descricao: "Temaki de salmão fresco com cream cheese",
    },
    {
      nome: "Hot Roll",
      preco: "R$ 24,90",
      img: "https://images.pexels.com/photos/2098085/pexels-photo-2098085.jpeg",
      descricao: "Uramaki empanado e frito, salmão, cream cheese",
    },
    {
      nome: "Yakisoba",
      preco: "R$ 28,90",
      img: "https://images.pexels.com/photos/5410400/pexels-photo-5410400.jpeg",
      descricao: "Macarrão oriental com frango e vegetais",
    },
  ],
  "Doce Encanto": [
    {
      nome: "Brownie Premium",
      preco: "R$ 14,90",
      img: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBsLqmXU_WztkBGMnmz0fjeexHUKHPkUSy5w&s",
      descricao: "Brownie artesanal com calda e sorvete",
    },
    {
      nome: "Cheesecake",
      preco: "R$ 16,90",
      img: "https://images.pexels.com/photos/140831/pexels-photo-140831.jpeg",
      descricao: "Cheesecake cremoso com frutas vermelhas",
    },
    {
      nome: "Milk Shake Oreo",
      preco: "R$ 12,90",
      img: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRoO-Sa3oXjmOAq2jptEqJ3RfOhMGKkYaWmNA&s",
      descricao: "Milk shake cremoso sabor oreo com chantilly",
    },
    {
      nome: "Torta Holandesa",
      preco: "R$ 18,90",
      img: "https://images.pexels.com/photos/1126359/pexels-photo-1126359.jpeg",
      descricao: "Torta com creme e cobertura de chocolate",
    },
  ],
};

// Sistema de carrinho
let carrinho = [];
let carrinhoTotal = 0;

// Função para adicionar item ao carrinho
function adicionarAoCarrinho(item, restaurante) {
  console.log("Adicionando ao carrinho:", item);

  // Verificar se item já existe no carrinho
  const itemExistente = carrinho.find(
    (cartItem) =>
      cartItem.nome === item.nome && cartItem.restaurante === restaurante
  );

  if (itemExistente) {
    itemExistente.quantidade++;
  } else {
    // Calcular preço correto (com desconto premium se aplicável)
    const precoFinal = isPremium
      ? calcularPrecoComDesconto(item.preco)
      : item.preco;

    carrinho.push({
      nome: item.nome,
      preco: item.preco,
      precoFinal: precoFinal,
      img: item.img,
      restaurante: restaurante,
      quantidade: 1,
    });
  }

  atualizarCarrinho();
  mostrarNotificacaoCarrinho(item.nome);
}

// Função para remover item do carrinho
function removerDoCarrinho(index) {
  carrinho.splice(index, 1);
  atualizarCarrinho();
}

// Função para alterar quantidade
function alterarQuantidade(index, delta) {
  if (carrinho[index]) {
    carrinho[index].quantidade += delta;

    if (carrinho[index].quantidade <= 0) {
      removerDoCarrinho(index);
    } else {
      atualizarCarrinho();
    }
  }
}

// Função para atualizar display do carrinho
function atualizarCarrinho() {
  const cartList = document.getElementById("cart-list");
  const cartBadge = document.getElementById("cart-badge");
  const cartSubtitle = document.getElementById("cart-subtitle");
  const cartTotal = document.getElementById("cart-total");
  const cartTotalValue = document.getElementById("cart-total-value");
  const cartCheckout = document.getElementById("cart-checkout");
  const cartEmpty = document.getElementById("cart-empty");

  // Calcular totais
  const totalItens = carrinho.reduce((sum, item) => sum + item.quantidade, 0);
  carrinhoTotal = carrinho.reduce((sum, item) => {
    const preco = parseFloat(
      item.precoFinal.replace("R$ ", "").replace(",", ".")
    );
    return sum + preco * item.quantidade;
  }, 0);

  // Atualizar badge
  if (totalItens > 0) {
    cartBadge.textContent = totalItens;
    cartBadge.style.display = "flex";
    cartSubtitle.textContent = `${totalItens} ${
      totalItens === 1 ? "item" : "itens"
    }`;
  } else {
    cartBadge.style.display = "none";
    cartSubtitle.textContent = "Carrinho vazio";
  }

  // Limpar lista
  cartList.innerHTML = "";

  if (carrinho.length === 0) {
    // Mostrar estado vazio
    cartList.innerHTML = `
      <div class="cart-empty">
        <p>🛒 Seu carrinho está vazio</p>
      </div>
    `;
    cartTotal.style.display = "none";
    cartCheckout.style.display = "none";
  } else {
    // Mostrar itens
    carrinho.forEach((item, index) => {
      const itemElement = document.createElement("article");
      itemElement.className = "cart-item";
      itemElement.style.position = "relative";

      itemElement.innerHTML = `
        <button class="cart-remove" onclick="removerDoCarrinho(${index})">&times;</button>
        <div class="cart-thumb">
          <img src="${item.img}" alt="${item.nome}" />
        </div>
        <div class="cart-info">
          <h4>${item.nome}</h4>
          <p class="cart-restaurant">${item.restaurante}</p>
          <p class="cart-price">${item.precoFinal}</p>
          ${
            isPremium && item.preco !== item.precoFinal
              ? `<span class="preco-original" style="font-size: 9px;">${item.preco}</span>`
              : ""
          }
        </div>
        <div class="cart-quantity">
          <button class="qty-btn" onclick="alterarQuantidade(${index}, -1)">-</button>
          <span>${item.quantidade}</span>
          <button class="qty-btn" onclick="alterarQuantidade(${index}, 1)">+</button>
        </div>
      `;

      cartList.appendChild(itemElement);
    });

    // Mostrar total e checkout
    cartTotalValue.textContent = `R$ ${carrinhoTotal
      .toFixed(2)
      .replace(".", ",")}`;
    cartTotal.style.display = "block";
    cartCheckout.style.display = "block";
  }
}

// Função para mostrar notificação quando item é adicionado
function mostrarNotificacaoCarrinho(nomeItem) {
  // Criar notificação temporária
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
  notificacao.innerHTML = `${nomeItem} adicionado ao carrinho!`;

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

// Função para calcular preço com desconto premium
function calcularPrecoComDesconto(preco) {
  const valor = parseFloat(preco.replace("R$ ", "").replace(",", "."));
  const valorComDesconto = valor * 0.8; // 20% de desconto
  return `R$ ${valorComDesconto.toFixed(2).replace(".", ",")}`;
}

// Função para filtrar restaurantes
function filtrarRestaurantes(categoria) {
  const cards = document.querySelectorAll(".restaurant-card");

  cards.forEach((card) => {
    const cardCategory = card.getAttribute("data-category");

    if (categoria === "todos" || cardCategory === categoria) {
      card.style.display = "flex";
      // Animar entrada
      card.style.opacity = "0";
      card.style.transform = "translateY(20px)";
      setTimeout(() => {
        card.style.transition = "all 0.3s ease";
        card.style.opacity = "1";
        card.style.transform = "translateY(0)";
      }, 100);
    } else {
      card.style.display = "none";
    }
  });
}

// Função para atualizar botões de filtro
function atualizarBotoesFiltro(categoriaAtiva) {
  const botoesFiltro = document.querySelectorAll(".filter-btn");

  botoesFiltro.forEach((botao) => {
    const categoria = botao.getAttribute("data-category");

    if (categoria === categoriaAtiva) {
      botao.classList.add("filter-btn-active");
    } else {
      botao.classList.remove("filter-btn-active");
    }
  });
}

// Função para buscar restaurantes
function buscarRestaurantes(termo) {
  const cards = document.querySelectorAll(".restaurant-card");
  const termoBusca = termo.toLowerCase().trim();

  if (termoBusca === "") {
    // Se busca vazia, mostrar todos os restaurantes
    cards.forEach((card) => {
      card.style.display = "flex";
      card.style.opacity = "1";
      card.style.transform = "translateY(0)";
    });
    return;
  }

  cards.forEach((card) => {
    const nomeRestaurante = card.querySelector("h2").textContent.toLowerCase();
    const tipoRestaurante = card
      .querySelector(".restaurant-type")
      .textContent.toLowerCase();
    const categoria = card.getAttribute("data-category").toLowerCase();

    // Verificar se o termo está no nome, tipo ou categoria
    const contemTermo =
      nomeRestaurante.includes(termoBusca) ||
      tipoRestaurante.includes(termoBusca) ||
      categoria.includes(termoBusca);

    if (contemTermo) {
      card.style.display = "flex";
      card.style.opacity = "0";
      card.style.transform = "translateY(20px)";
      setTimeout(() => {
        card.style.transition = "all 0.3s ease";
        card.style.opacity = "1";
        card.style.transform = "translateY(0)";
      }, 100);
    } else {
      card.style.display = "none";
    }
  });

  // Mostrar mensagem se nenhum resultado encontrado
  const resultadosVisiveis = Array.from(cards).some(
    (card) => card.style.display === "flex" || card.style.display === ""
  );

  let mensagemSemResultados = document.querySelector(".sem-resultados");
  if (!resultadosVisiveis) {
    if (!mensagemSemResultados) {
      mensagemSemResultados = document.createElement("div");
      mensagemSemResultados.className = "sem-resultados";
      mensagemSemResultados.innerHTML = `
        <div style="text-align: center; padding: 40px; color: #6b7280;">
          <div style="font-size: 48px; margin-bottom: 16px;">🔍</div>
          <h3 style="color: #111827; margin-bottom: 8px;">Nenhum restaurante encontrado</h3>
          <p>Tente buscar por "${termo}" ou explore outras categorias</p>
        </div>
      `;
      document
        .querySelector(".restaurants-grid")
        .appendChild(mensagemSemResultados);
    }
  } else if (mensagemSemResultados) {
    mensagemSemResultados.remove();
  }
}

// Função para limpar busca e filtros
function limparBusca() {
  const searchInput = document.querySelector(".search-wrapper input");
  searchInput.value = "";
  buscarRestaurantes("");

  // Reativar filtro "Todos"
  atualizarBotoesFiltro("todos");
  filtrarRestaurantes("todos");
}

// Função para fechar popup do cardápio
function fecharCardapio() {
  const popup = document.querySelector(".cardapio-popup");
  if (popup) {
    popup.style.opacity = "0";

    setTimeout(() => {
      popup.remove();
      document.body.style.overflow = "auto";
    }, 200);
  }
}

// Função para abrir popup do cardápio
function abrirCardapio(nomeRestaurante) {
  const itens = cardapios[nomeRestaurante] || [];

  const popup = document.createElement("div");
  popup.className = "cardapio-popup";

  // HTML mais simples e direto
  const itensHTML = itens
    .map((item, index) => {
      const preco = isPremium
        ? calcularPrecoComDesconto(item.preco)
        : item.preco;
      const precoOriginal = isPremium
        ? `<span class="preco-original">${item.preco}</span>`
        : "";

      return `
      <div class="cardapio-item">
        <img src="${item.img}" alt="${item.nome}" class="cardapio-img" loading="lazy">
        <div class="cardapio-info">
          <h3>${item.nome}</h3>
          <p class="cardapio-descricao">${item.descricao}</p>
          <div class="cardapio-preco-area">
            <span class="cardapio-preco">${preco}</span>
            ${precoOriginal}
            <button class="btn-adicionar" data-item-index="${index}" data-restaurante="${nomeRestaurante}">
              Adicionar
            </button>
          </div>
        </div>
      </div>`;
    })
    .join("");

  popup.innerHTML = `
    <div class="cardapio-content">
      <div class="cardapio-header">
        <h2>Cardápio - ${nomeRestaurante}</h2>
        <button class="cardapio-close">&times;</button>
      </div>
      <div class="cardapio-lista">${itensHTML}</div>
    </div>
  `;

  // Adicionar ao DOM primeiro
  document.body.appendChild(popup);
  document.body.style.overflow = "hidden";

  // Event listeners
  popup.querySelector(".cardapio-close").onclick = fecharCardapio;
  popup.onclick = (e) => e.target === popup && fecharCardapio();

  // Event listeners para botões adicionar
  popup.querySelectorAll(".btn-adicionar").forEach((button) => {
    button.addEventListener("click", (e) => {
      const itemIndex = parseInt(e.target.getAttribute("data-item-index"));
      const restaurante = e.target.getAttribute("data-restaurante");
      const item = itens[itemIndex];

      adicionarAoCarrinho(item, restaurante);
    });
  });

  // Otimização de scroll - desabilitar hover durante scroll
  let isScrolling = false;
  let scrollTimeout;
  const lista = popup.querySelector(".cardapio-lista");

  lista.addEventListener(
    "scroll",
    () => {
      if (!isScrolling) {
        lista.style.pointerEvents = "none";
        isScrolling = true;
      }

      clearTimeout(scrollTimeout);
      scrollTimeout = setTimeout(() => {
        lista.style.pointerEvents = "auto";
        isScrolling = false;
      }, 100);
    },
    { passive: true }
  );

  // Animação simples e rápida
  popup.style.opacity = "0";
  popup.style.display = "flex";

  requestAnimationFrame(() => {
    popup.style.transition = "opacity 0.2s ease";
    popup.style.opacity = "1";
  });

  // ESC key
  const handleEsc = (e) => {
    if (e.key === "Escape") {
      fecharCardapio();
      document.removeEventListener("keydown", handleEsc);
    }
  };
  document.addEventListener("keydown", handleEsc);
}

// Função para finalizar pedido
function finalizarPedido() {
  if (carrinho.length === 0) {
    alert("Seu carrinho está vazio!");
    return;
  }

  // Criar objeto do pedido
  const pedido = {
    id: Date.now(), // ID único baseado no timestamp
    data: new Date().toISOString(),
    itens: carrinho.map((item) => ({
      nome: item.nome,
      preco: item.preco,
      precoFinal: item.precoFinal,
      img: item.img,
      restaurante: item.restaurante,
      quantidade: item.quantidade,
    })),
    total: carrinhoTotal,
    status: "preparando",
    clienteId: usuarioAtual?.clienteId || null,
    clienteNome: usuarioAtual?.nome || "Cliente",
  };

  // Buscar histórico existente
  let historicoPedidos = JSON.parse(
    localStorage.getItem("historicoPedidos") || "[]"
  );

  // Adicionar novo pedido no início
  historicoPedidos.unshift(pedido);

  // Salvar no localStorage
  localStorage.setItem("historicoPedidos", JSON.stringify(historicoPedidos));

  alert(
    "✅ Pedido enviado com sucesso!\n\n" +
      `Pedido #${pedido.id}\n` +
      `Total: R$ ${carrinhoTotal.toFixed(2).replace(".", ",")}\n\n` +
      "Acompanhe seu pedido no Histórico de Pedidos!"
  );

  // Limpar carrinho após enviar pedido
  carrinho = [];
  carrinhoTotal = 0;
  atualizarCarrinho();
}

// Inicializar quando a página carregar
document.addEventListener("DOMContentLoaded", () => {
  console.log("🚀 Inicializando página de restaurantes...");

  // Verificar usuário e premium
  verificarUsuarioLogado();

  // Inicializar carrinho vazio
  atualizarCarrinho();

  // Event listeners para busca
  const searchInput = document.querySelector(".search-wrapper input");
  const searchBtn = document.querySelector(".search-btn");

  // Busca em tempo real enquanto digita
  searchInput.addEventListener("input", (e) => {
    const termo = e.target.value;
    buscarRestaurantes(termo);

    // Limpar filtros quando buscar
    if (termo.trim() !== "") {
      document.querySelectorAll(".filter-btn").forEach((btn) => {
        btn.classList.remove("filter-btn-active");
      });
    }
  });

  // Busca ao clicar no botão
  searchBtn.addEventListener("click", () => {
    const termo = searchInput.value;
    buscarRestaurantes(termo);
  });

  // Busca ao pressionar Enter
  searchInput.addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      const termo = e.target.value;
      buscarRestaurantes(termo);
    }
  });

  // Event listener para botão finalizar pedido
  setTimeout(() => {
    const checkoutButton = document.getElementById("cart-checkout");
    if (checkoutButton) {
      checkoutButton.addEventListener("click", finalizarPedido);
    }
  }, 100);

  // Event listeners para filtros (modificado para interagir com busca)
  document.querySelectorAll(".filter-btn").forEach((button) => {
    button.addEventListener("click", (e) => {
      const categoria = button.getAttribute("data-category");

      // Limpar busca ao usar filtros
      const searchInput = document.querySelector(".search-wrapper input");
      searchInput.value = "";

      // Remover mensagem de "sem resultados" se existir
      const mensagemSemResultados = document.querySelector(".sem-resultados");
      if (mensagemSemResultados) {
        mensagemSemResultados.remove();
      }

      // Atualizar visual dos botões
      atualizarBotoesFiltro(categoria);

      // Filtrar restaurantes
      filtrarRestaurantes(categoria);

      console.log("Filtro aplicado:", categoria);
    });
  });

  // Aguardar um pouco para garantir que o DOM está completamente carregado
  setTimeout(() => {
    // Event listener para botões de cardápio - APENAS popup, sem redirecionamento
    const botoes = document.querySelectorAll(".btn-cardapio");
    console.log("Encontrados", botoes.length, "botões de cardápio");

    botoes.forEach((button, index) => {
      console.log("Configurando botão", index);

      // Remover qualquer atributo que possa causar navegação
      button.removeAttribute("href");
      button.removeAttribute("onclick");
      button.type = "button"; // Garantir que é um botão

      // Adicionar event listener
      button.addEventListener("click", (e) => {
        console.log("Botão clicado:", button);
        e.preventDefault(); // Prevenir qualquer navegação
        e.stopPropagation(); // Parar propagação do evento
        e.stopImmediatePropagation(); // Parar todos os outros listeners

        const card = button.closest(".restaurant-card");
        const nomeRestaurante = card.querySelector("h2").textContent;
        console.log("Abrindo cardápio para:", nomeRestaurante);
        abrirCardapio(nomeRestaurante);
      });

      // Limpar qualquer onclick inline
      button.onclick = null;
    });
  }, 100);
});

// Tornar funções globais para uso nos event handlers inline
window.fecharCardapio = fecharCardapio;
window.abrirCardapio = abrirCardapio;
window.removerDoCarrinho = removerDoCarrinho;
window.alterarQuantidade = alterarQuantidade;
window.finalizarPedido = finalizarPedido;
window.buscarRestaurantes = buscarRestaurantes;
window.limparBusca = limparBusca;
window.fecharBannerPremium = fecharBannerPremium;
