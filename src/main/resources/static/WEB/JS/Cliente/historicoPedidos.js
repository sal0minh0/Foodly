// Variáveis globais
let historicoPedidos = [];
let ordenacaoAtual = "data-desc";
let intervaloAtualizacao = null;

// Carregar pedidos do localStorage
function carregarPedidos() {
  historicoPedidos = JSON.parse(
    localStorage.getItem("historicoPedidos") || "[]"
  );
  atualizarStatusPedidos();
}

// Atualizar status dos pedidos baseado no tempo
function atualizarStatusPedidos() {
  let houveMudanca = false;

  historicoPedidos = historicoPedidos.map((pedido) => {
    if (pedido.status === "preparando") {
      const minutosPassados = (Date.now() - pedido.id) / (1000 * 60);

      // Após 50 minutos, muda para "entregue"
      if (minutosPassados > 50) {
        pedido.status = "entregue";
        houveMudanca = true;
        console.log(`📦 Pedido #${pedido.id} foi entregue!`);
      }
    }
    return pedido;
  });

  // Salvar mudanças
  if (houveMudanca) {
    localStorage.setItem("historicoPedidos", JSON.stringify(historicoPedidos));
  }

  return houveMudanca;
}

// Verificar e atualizar status periodicamente
function iniciarAtualizacaoAutomatica() {
  // Verificar a cada 30 segundos
  intervaloAtualizacao = setInterval(() => {
    const houveMudanca = atualizarStatusPedidos();

    if (houveMudanca) {
      // Atualizar interface se houve mudança
      calcularEstatisticas();
      renderizarPedidos();

      // Mostrar notificação
      mostrarNotificacaoStatus();
    }
  }, 30000); // 30 segundos
}

// Mostrar notificação de mudança de status
function mostrarNotificacaoStatus() {
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
  notificacao.innerHTML = "✅ Um pedido foi entregue!";

  document.body.appendChild(notificacao);

  setTimeout(() => {
    notificacao.style.transform = "translateX(0)";
  }, 100);

  setTimeout(() => {
    notificacao.style.transform = "translateX(100%)";
    setTimeout(() => notificacao.remove(), 300);
  }, 4000);
}

// Formatar data
function formatarData(dataISO) {
  const data = new Date(dataISO);
  const opcoes = {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  };
  return data.toLocaleDateString("pt-BR", opcoes);
}

// Formatar preço
function formatarPreco(valor) {
  if (typeof valor === "string") {
    return valor;
  }
  return `R$ ${valor.toFixed(2).replace(".", ",")}`;
}

// Calcular estatísticas baseado nos pedidos do localStorage
function calcularEstatisticas() {
  // Se não há pedidos, mostrar valores zerados
  if (historicoPedidos.length === 0) {
    document.getElementById("total-pedidos").textContent = "0";
    document.getElementById("total-gasto").textContent = "R$ 0,00";
    document.getElementById("restaurante-favorito").textContent = "-";
    document.getElementById("item-favorito").textContent = "-";
    return;
  }

  // Total de pedidos
  const totalPedidos = historicoPedidos.length;
  document.getElementById("total-pedidos").textContent = totalPedidos;

  // Total gasto (soma de todos os pedidos)
  let totalGasto = 0;
  historicoPedidos.forEach((pedido) => {
    if (pedido.total) {
      totalGasto += pedido.total;
    }
  });
  document.getElementById("total-gasto").textContent =
    formatarPreco(totalGasto);

  // Restaurante favorito (mais pedidos)
  const restaurantes = {};
  historicoPedidos.forEach((pedido) => {
    if (pedido.itens && Array.isArray(pedido.itens)) {
      pedido.itens.forEach((item) => {
        if (item.restaurante) {
          restaurantes[item.restaurante] =
            (restaurantes[item.restaurante] || 0) + item.quantidade;
        }
      });
    }
  });

  let restauranteFavorito = "-";
  let maxRestaurante = 0;
  Object.entries(restaurantes).forEach(([nome, quantidade]) => {
    if (quantidade > maxRestaurante) {
      maxRestaurante = quantidade;
      restauranteFavorito = nome;
    }
  });
  document.getElementById("restaurante-favorito").textContent =
    restauranteFavorito;

  // Item/Prato favorito (mais pedido)
  const itens = {};
  historicoPedidos.forEach((pedido) => {
    if (pedido.itens && Array.isArray(pedido.itens)) {
      pedido.itens.forEach((item) => {
        if (item.nome) {
          itens[item.nome] = (itens[item.nome] || 0) + item.quantidade;
        }
      });
    }
  });

  let itemFavorito = "-";
  let maxItem = 0;
  Object.entries(itens).forEach(([nome, quantidade]) => {
    if (quantidade > maxItem) {
      maxItem = quantidade;
      itemFavorito = nome;
    }
  });
  document.getElementById("item-favorito").textContent = itemFavorito;
}

// Ordenar pedidos
function ordenarPedidos() {
  let pedidosOrdenados = [...historicoPedidos];

  switch (ordenacaoAtual) {
    case "data-desc":
      pedidosOrdenados.sort((a, b) => new Date(b.data) - new Date(a.data));
      break;
    case "data-asc":
      pedidosOrdenados.sort((a, b) => new Date(a.data) - new Date(b.data));
      break;
    case "valor-desc":
      pedidosOrdenados.sort((a, b) => (b.total || 0) - (a.total || 0));
      break;
    case "valor-asc":
      pedidosOrdenados.sort((a, b) => (a.total || 0) - (b.total || 0));
      break;
  }

  return pedidosOrdenados;
}

// Renderizar pedidos na lista
function renderizarPedidos() {
  const ordersList = document.getElementById("ordersList");
  const emptyState = document.getElementById("emptyState");
  const pedidosOrdenados = ordenarPedidos();

  // Mostrar estado vazio se não houver pedidos
  if (pedidosOrdenados.length === 0) {
    ordersList.innerHTML = "";
    emptyState.style.display = "block";
    return;
  }

  emptyState.style.display = "none";

  ordersList.innerHTML = pedidosOrdenados
    .map((pedido) => {
      const statusClass = `status-${pedido.status}`;
      const statusTexto =
        {
          preparando: "👨‍🍳 Preparando",
          entregue: "✅ Entregue",
          cancelado: "❌ Cancelado",
        }[pedido.status] || pedido.status;

      // Gerar HTML dos itens
      let itensHTML = "";
      if (pedido.itens && Array.isArray(pedido.itens)) {
        itensHTML = pedido.itens
          .map(
            (item) => `
        <div class="item-row">
          <div class="item-info">
            <img src="${item.img || "https://via.placeholder.com/40"}" alt="${
              item.nome
            }" class="item-image">
            <div class="item-details">
              <h5>${item.nome}</h5>
              <p>${item.restaurante}</p>
            </div>
          </div>
          <div class="item-price">
            <span class="quantity">x${item.quantidade}</span>
            <span class="price">${item.precoFinal || item.preco}</span>
          </div>
        </div>
      `
          )
          .join("");
      }

      return `
      <article class="order-card" data-pedido-id="${pedido.id}">
        <div class="order-header">
          <div class="order-info">
            <h3>Pedido #${pedido.id}</h3>
            <span class="order-date">${formatarData(pedido.data)}</span>
          </div>
          <span class="order-status ${statusClass}">${statusTexto}</span>
        </div>

        <div class="order-items">
          <h4>Itens do pedido:</h4>
          <div class="items-list">
            ${itensHTML}
          </div>
        </div>

        <div class="order-footer">
          <span class="order-total">Total: ${formatarPreco(pedido.total)}</span>
          <div class="order-actions">
            <button class="btn-reorder" onclick="refazerPedido(${pedido.id})">
              🔄 Pedir Novamente
            </button>
            <button class="btn-details" onclick="verDetalhes(${pedido.id})">
              📋 Detalhes
            </button>
          </div>
        </div>
      </article>
    `;
    })
    .join("");
}

// Refazer pedido - adiciona itens ao carrinho
function refazerPedido(pedidoId) {
  const pedido = historicoPedidos.find((p) => p.id === pedidoId);

  if (!pedido) {
    alert("Pedido não encontrado!");
    return;
  }

  // Carregar carrinho existente
  let carrinho = JSON.parse(localStorage.getItem("carrinho") || "[]");

  // Adicionar itens do pedido ao carrinho
  if (pedido.itens && Array.isArray(pedido.itens)) {
    pedido.itens.forEach((item) => {
      const itemExistente = carrinho.find(
        (c) => c.nome === item.nome && c.restaurante === item.restaurante
      );

      if (itemExistente) {
        itemExistente.quantidade += item.quantidade;
      } else {
        carrinho.push({
          nome: item.nome,
          preco: item.preco,
          precoFinal: item.precoFinal || item.preco,
          img: item.img,
          restaurante: item.restaurante,
          quantidade: item.quantidade,
        });
      }
    });
  }

  // Salvar carrinho
  localStorage.setItem("carrinho", JSON.stringify(carrinho));

  alert(
    "✅ Itens adicionados ao carrinho!\n\nRedirecionando para restaurantes..."
  );
  window.location.href = "../../html/Foodly/restaurantes.html";
}

// Ver detalhes do pedido
function verDetalhes(pedidoId) {
  const pedido = historicoPedidos.find((p) => p.id === pedidoId);

  if (!pedido) {
    alert("Pedido não encontrado!");
    return;
  }

  let detalhes = "";
  if (pedido.itens && Array.isArray(pedido.itens)) {
    detalhes = pedido.itens
      .map(
        (item) =>
          `• ${item.nome} (x${item.quantidade}) - ${
            item.precoFinal || item.preco
          }`
      )
      .join("\n");
  }

  // Formatar status com letra maiúscula
  const statusFormatado =
    {
      preparando: "Preparando",
      entregue: "Entregue",
      cancelado: "Cancelado",
    }[pedido.status] || pedido.status;

  alert(
    `📋 Detalhes do Pedido #${pedido.id}\n\n` +
      `Data: ${formatarData(pedido.data)}\n` +
      `Status: ${statusFormatado}\n` +
      `Cliente: ${pedido.clienteNome || "Cliente"}\n\n` +
      `Itens:\n${detalhes}\n\n` +
      `Total: ${formatarPreco(pedido.total)}`
  );
}

// Limpar histórico de pedidos
function limparHistorico() {
  if (historicoPedidos.length === 0) {
    alert("O histórico já está vazio!");
    return;
  }

  if (
    confirm(
      "⚠️ Tem certeza que deseja limpar todo o histórico de pedidos?\n\nEsta ação não pode ser desfeita!"
    )
  ) {
    // Limpar do localStorage
    localStorage.removeItem("historicoPedidos");
    historicoPedidos = [];

    // Atualizar interface
    calcularEstatisticas();
    renderizarPedidos();

    alert("✅ Histórico limpo com sucesso!");
  }
}

// Inicializar página
document.addEventListener("DOMContentLoaded", () => {
  console.log("📋 Carregando histórico de pedidos...");

  // Carregar pedidos do localStorage
  carregarPedidos();
  console.log("Pedidos carregados:", historicoPedidos.length);

  // Calcular e exibir estatísticas
  calcularEstatisticas();

  // Renderizar lista de pedidos
  renderizarPedidos();

  // Iniciar atualização automática de status
  iniciarAtualizacaoAutomatica();

  // Event listener para ordenação
  const sortSelect = document.getElementById("sort-select");
  if (sortSelect) {
    sortSelect.addEventListener("change", (e) => {
      ordenacaoAtual = e.target.value;
      renderizarPedidos();
    });
  }

  // Event listener para limpar histórico
  const clearHistoryBtn = document.getElementById("clearHistoryBtn");
  if (clearHistoryBtn) {
    clearHistoryBtn.addEventListener("click", limparHistorico);
  }
});

// Limpar intervalo quando sair da página
window.addEventListener("beforeunload", () => {
  if (intervaloAtualizacao) {
    clearInterval(intervaloAtualizacao);
  }
});

// Funções globais para os botões
window.refazerPedido = refazerPedido;
window.verDetalhes = verDetalhes;
window.limparHistorico = limparHistorico;
