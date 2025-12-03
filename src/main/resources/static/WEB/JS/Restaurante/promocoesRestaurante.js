if (window.location.pathname.includes("promocoesRestaurante.html")) {
  const API_URL = "http://localhost:8080/api";

  // Elementos do DOM
  const btnAdicionarPromocao = document.getElementById(
    "btn-adicionar-promocao"
  );
  const modal = document.getElementById("modal-promocao");
  const modalClose = document.getElementById("modal-close");
  const btnCancelar = document.getElementById("btn-cancelar");
  const formPromocao = document.getElementById("form-promocao");
  const promocoesLista = document.getElementById("promocoes-lista");
  const searchInput = document.getElementById("search-input");
  const filterStatus = document.getElementById("filter-status");
  const modalTitle = document.getElementById("modal-title");

  let promocoes = [];
  let promocaoEditando = null;
  let restauranteAtual = null;

  // Carregar dados do restaurante
  async function carregarRestaurante() {
    try {
      const usuarioJson = localStorage.getItem("usuario");
      if (!usuarioJson) {
        alert("Sessão expirada. Faça login novamente.");
        window.location.href = "../../html/escolhaPerfil.html";
        return;
      }

      const usuario = JSON.parse(usuarioJson);
      if (usuario.tipoUsuario !== "restaurante") {
        alert("Acesso negado. Esta página é apenas para restaurantes.");
        window.location.href = "../../html/escolhaPerfil.html";
        return;
      }

      // Buscar restaurante
      const response = await fetch(`${API_URL}/restaurantes`);
      if (response.ok) {
        const restaurantes = await response.json();
        restauranteAtual = restaurantes.find(
          (r) => r.usuarioId === usuario.usuarioId
        );

        if (restauranteAtual) {
          await carregarPromocoes();
        }
      }
    } catch (error) {
      console.error("Erro ao carregar restaurante:", error);
    }
  }

  // Carregar promoções da API
  async function carregarPromocoes() {
    try {
      const response = await fetch(
        `${API_URL}/promocoes/restaurante/${restauranteAtual.id}`
      );
      if (response.ok) {
        promocoes = await response.json();
        renderizarPromocoes();
      }
    } catch (error) {
      console.error("Erro ao carregar promoções:", error);
      promocoes = [];
      renderizarPromocoes();
    }
  }

  // Formatar preço para exibição
  function formatarPreco(valor) {
    return valor.toLocaleString("pt-BR", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
  }

  // Converter preço brasileiro para número
  function converterPrecoParaNumero(precoStr) {
    return parseFloat(precoStr.replace(/[R$\s]/g, "").replace(",", "."));
  }

  // Formatar data para exibição (padrão brasileiro)
  function formatarData(dataStr) {
    const data = new Date(dataStr);
    const dia = String(data.getDate()).padStart(2, "0");
    const mes = String(data.getMonth() + 1).padStart(2, "0");
    const ano = data.getFullYear();
    const hora = String(data.getHours()).padStart(2, "0");
    const minuto = String(data.getMinutes()).padStart(2, "0");

    return `${dia}/${mes}/${ano} às ${hora}h${minuto}`;
  }

  // Renderizar promoções
  function renderizarPromocoes() {
    let promocoesFiltradas = filtrarPromocoes();

    if (promocoesFiltradas.length === 0) {
      promocoesLista.innerHTML = `
        <div class="empty-state">
          <div class="empty-icon">🎁</div>
          <h3>Nenhuma promoção encontrada</h3>
          <p>Adicione uma nova promoção</p>
        </div>
      `;
      return;
    }

    promocoesLista.innerHTML = promocoesFiltradas
      .map((promocao) => {
        const valorFormatado =
          promocao.tipoDesconto === "percentual"
            ? `${formatarPreco(promocao.valorDesconto)}% OFF`
            : `R$ ${formatarPreco(promocao.valorDesconto)} OFF`;

        // Verificar se a promoção expirou
        const agora = new Date();
        const dataFim = new Date(promocao.dataFim);
        const expirou = dataFim < agora;

        // Status real: combina campo 'ativo' e se expirou
        const statusTexto = expirou
          ? "Expirada"
          : promocao.ativo
          ? "Ativa"
          : "Inativa";
        const statusClass = expirou
          ? "expirada"
          : promocao.ativo
          ? "ativo"
          : "inativo";

        return `
        <div class="produto-card ${expirou ? "card-expirado" : ""}">
          <div class="produto-header">
            <div class="produto-info">
              <h3>${promocao.titulo}</h3>
              <span class="produto-categoria">${
                promocao.tipoDesconto === "percentual"
                  ? "Percentual"
                  : "Valor Fixo"
              }</span>
            </div>
            <div class="produto-status">
              <span class="status-badge ${statusClass}">
                ${statusTexto}
              </span>
            </div>
          </div>
          <p class="produto-descricao">${
            promocao.descricao || "Sem descrição"
          }</p>
          <div style="margin: 12px 0; font-size: 13px; color: #6b7280;">
            <div><strong style="">Início:</strong> ${formatarData(promocao.dataInicio)}</div>
            <div><strong>Fim:</strong> ${formatarData(promocao.dataFim)}</div>
            ${
              expirou
                ? '<div style="color: #dc3545; font-weight: 600;">⚠️ Esta promoção expirou</div>'
                : ""
            }
          </div>
          <div class="produto-footer">
            <span class="produto-preco">${valorFormatado}</span>
            <div class="produto-acoes">
              <button class="btn-icon edit" onclick="editarPromocao(${
                promocao.id
              })" title="Editar">
                <img src="../../assets/edit.svg" alt="Edit" />
              </button>
              <button class="btn-icon delete" onclick="deletarPromocao(${
                promocao.id
              })" title="Excluir">
                <img src="../../assets/delete2.svg" alt="Delete" />
              </button>
            </div>
          </div>
        </div>
      `;
      })
      .join("");
  }

  // Filtrar promoções
  function filtrarPromocoes() {
    let resultado = promocoes;

    // Filtrar por busca
    const termoBusca = searchInput.value.toLowerCase();
    if (termoBusca) {
      resultado = resultado.filter(
        (p) =>
          p.titulo.toLowerCase().includes(termoBusca) ||
          (p.descricao && p.descricao.toLowerCase().includes(termoBusca))
      );
    }

    // Filtrar por status
    const status = filterStatus.value;
    if (status === "ativos") {
      resultado = resultado.filter((p) => p.ativo);
    } else if (status === "inativos") {
      resultado = resultado.filter((p) => !p.ativo);
    }

    return resultado;
  }

  // Abrir modal para adicionar
  function abrirModalAdicionar() {
    promocaoEditando = null;
    modalTitle.textContent = "Adicionar Promoção";
    formPromocao.reset();
    document.getElementById("promocao-ativo").checked = true;
    modal.classList.add("active");
  }

  // Editar promoção
  window.editarPromocao = function (id) {
    promocaoEditando = promocoes.find((p) => p.id === id);
    if (!promocaoEditando) return;

    modalTitle.textContent = "Editar Promoção";
    document.getElementById("promocao-titulo").value = promocaoEditando.titulo;
    document.getElementById("promocao-descricao").value =
      promocaoEditando.descricao || "";
    document.getElementById("promocao-tipo").value =
      promocaoEditando.tipoDesconto;
    document.getElementById("promocao-valor").value = formatarPreco(
      promocaoEditando.valorDesconto
    );
    document.getElementById("promocao-inicio").value = new Date(
      promocaoEditando.dataInicio
    )
      .toISOString()
      .slice(0, 16);
    document.getElementById("promocao-fim").value = new Date(
      promocaoEditando.dataFim
    )
      .toISOString()
      .slice(0, 16);
    document.getElementById("promocao-ativo").checked = promocaoEditando.ativo;

    modal.classList.add("active");
  };

  // Deletar promoção
  window.deletarPromocao = async function (id) {
    if (!confirm("Tem certeza que deseja excluir esta promoção?")) return;

    try {
      const response = await fetch(`${API_URL}/promocoes/${id}`, {
        method: "DELETE",
      });

      if (response.ok) {
        alert("Promoção excluída com sucesso!");
        await carregarPromocoes();
      } else {
        const error = await response.json();
        alert(error.message || "Erro ao excluir promoção");
      }
    } catch (error) {
      console.error("Erro:", error);
      alert("Erro ao excluir promoção");
    }
  };

  // Fechar modal
  function fecharModal() {
    modal.classList.remove("active");
    promocaoEditando = null;
  }

  // Salvar promoção
  async function salvarPromocao(event) {
    event.preventDefault();

    const titulo = document.getElementById("promocao-titulo").value.trim();
    const descricao = document
      .getElementById("promocao-descricao")
      .value.trim();
    const tipoDesconto = document.getElementById("promocao-tipo").value;
    const valorStr = document.getElementById("promocao-valor").value.trim();
    const dataInicioStr = document.getElementById("promocao-inicio").value;
    const dataFimStr = document.getElementById("promocao-fim").value;
    const ativo = document.getElementById("promocao-ativo").checked;

    const valorDesconto = converterPrecoParaNumero(valorStr);

    if (!titulo || !valorStr || valorDesconto <= 0 || isNaN(valorDesconto)) {
      alert("Preencha todos os campos obrigatórios corretamente!");
      return;
    }

    if (!dataInicioStr || !dataFimStr) {
      alert("Preencha as datas de início e fim!");
      return;
    }

    // Converter para LocalDateTime sem alterar timezone
    // Formato do input: "2025-11-25T14:30"
    // Adicionar segundos para ficar: "2025-11-25T14:30:00"
    const dataInicio = dataInicioStr.includes(":")
      ? dataInicioStr + ":00"
      : dataInicioStr;
    const dataFim = dataFimStr.includes(":") ? dataFimStr + ":00" : dataFimStr;

    try {
      if (promocaoEditando) {
        // Atualizar promoção existente
        const response = await fetch(`${API_URL}/promocoes/atualizar`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            id: promocaoEditando.id,
            titulo,
            descricao,
            tipoDesconto,
            valorDesconto,
            dataInicio,
            dataFim,
            ativo,
          }),
        });

        if (response.ok) {
          alert("Promoção atualizada com sucesso!");
          await carregarPromocoes();
        } else {
          const error = await response.json();
          alert(error.message || "Erro ao atualizar promoção");
        }
      } else {
        // Adicionar nova promoção
        const response = await fetch(`${API_URL}/promocoes/adicionar`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            restauranteId: restauranteAtual.id,
            titulo,
            descricao,
            tipoDesconto,
            valorDesconto,
            dataInicio,
            dataFim,
            ativo,
          }),
        });

        if (response.ok) {
          alert("Promoção adicionada com sucesso!");
          await carregarPromocoes();
        } else {
          const error = await response.json();
          alert(error.message || "Erro ao adicionar promoção");
        }
      }

      fecharModal();
    } catch (error) {
      console.error("Erro:", error);
      alert("Erro ao salvar promoção");
    }
  }

  // Event Listeners
  if (btnAdicionarPromocao)
    btnAdicionarPromocao.addEventListener("click", abrirModalAdicionar);
  if (modalClose) modalClose.addEventListener("click", fecharModal);
  if (btnCancelar) btnCancelar.addEventListener("click", fecharModal);
  if (formPromocao) formPromocao.addEventListener("submit", salvarPromocao);
  if (searchInput) searchInput.addEventListener("input", renderizarPromocoes);
  if (filterStatus)
    filterStatus.addEventListener("change", renderizarPromocoes);

  // Fechar modal ao clicar fora
  if (modal) {
    modal.addEventListener("click", (e) => {
      if (e.target === modal) {
        fecharModal();
      }
    });
  }

  // Inicializar
  document.addEventListener("DOMContentLoaded", () => {
    carregarRestaurante();
  });
}
