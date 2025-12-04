if (window.location.pathname.includes("gerenciarCardapio.html")) {
  const API_URL = CONFIG.API_URL;

  // Elementos do DOM
  const btnAdicionarProduto = document.getElementById("btn-adicionar-produto");
  const modal = document.getElementById("modal-produto");
  const modalClose = document.getElementById("modal-close");
  const btnCancelar = document.getElementById("btn-cancelar");
  const formProduto = document.getElementById("form-produto");
  const produtosLista = document.getElementById("produtos-lista");
  const searchInput = document.getElementById("search-input");
  const filterStatus = document.getElementById("filter-status");
  const modalTitle = document.getElementById("modal-title");

  let produtos = [];
  let produtoEditando = null;
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
          await carregarProdutos();
        }
      }
    } catch (error) {
      console.error("Erro ao carregar restaurante:", error);
    }
  }

  // Carregar produtos da API
  async function carregarProdutos() {
    try {
      const response = await fetch(
        `${API_URL}/cardapio/restaurante/${restauranteAtual.id}`
      );
      if (response.ok) {
        produtos = await response.json();
        renderizarProdutos();
      }
    } catch (error) {
      console.error("Erro ao carregar produtos:", error);
      produtos = [];
      renderizarProdutos();
    }
  }

  // Formatar preço para exibição (padrão brasileiro)
  function formatarPreco(valor) {
    return valor.toLocaleString("pt-BR", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
  }

  // Converter preço brasileiro para número
  function converterPrecoParaNumero(precoStr) {
    // Remove o R$ e espaços, substitui vírgula por ponto
    return parseFloat(precoStr.replace(/[R$\s]/g, "").replace(",", "."));
  }

  // Formatar input de preço em tempo real
  if (document.getElementById("produto-preco")) {
    const inputPreco = document.getElementById("produto-preco");

    inputPreco.addEventListener("input", function (e) {
      let valor = e.target.value;

      // Remove tudo que não é número ou vírgula
      valor = valor.replace(/[^\d,]/g, "");

      // Permite apenas uma vírgula
      const partes = valor.split(",");
      if (partes.length > 2) {
        valor = partes[0] + "," + partes.slice(1).join("");
      }

      // Limita a 2 casas decimais após a vírgula
      if (partes[1] && partes[1].length > 2) {
        valor = partes[0] + "," + partes[1].substring(0, 2);
      }

      e.target.value = valor;
    });
  }

  // Renderizar produtos
  function renderizarProdutos() {
    let produtosFiltrados = filtrarProdutos();

    if (produtosFiltrados.length === 0) {
      produtosLista.innerHTML = `
        <div class="empty-state">
          <div class="empty-icon">📦</div>
          <h3>Nenhum produto encontrado</h3>
          <p>Tente ajustar os filtros ou adicione um novo produto</p>
        </div>
      `;
      return;
    }

    produtosLista.innerHTML = produtosFiltrados
      .map((produto) => {
        const imagemHtml = produto.imagem
          ? `<img src="http://localhost:8080/uploads/produtos/${produto.imagem}" class="produto-imagem" alt="${produto.nome}" />`
          : `<div class="produto-sem-imagem">🍽️</div>`;

        return `
        <div class="produto-card">
          ${imagemHtml}
          <div class="produto-header">
            <div class="produto-info">
              <h3>${produto.nome}</h3>
              <span class="produto-categoria">${produto.categoria}</span>
            </div>
            <div class="produto-status">
              <span class="status-badge ${produto.ativo ? "ativo" : "inativo"}">
                ${produto.ativo ? "Ativo" : "Inativo"}
              </span>
            </div>
          </div>
          <p class="produto-descricao">${
            produto.descricao || "Sem descrição"
          }</p>
          <div class="produto-footer">
            <span class="produto-preco">R$ ${formatarPreco(
              produto.preco
            )}</span>
            <div class="produto-acoes">
              <button class="btn-icon edit" onclick="editarProduto(${
                produto.id
              })" title="Editar">
                <img src="../../assets/edit.svg" alt="Edit" />
              </button>
              <button class="btn-icon delete" onclick="deletarProduto(${
                produto.id
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

  // Filtrar produtos
  function filtrarProdutos() {
    let resultado = produtos;

    // Filtrar por busca
    const termoBusca = searchInput.value.toLowerCase();
    if (termoBusca) {
      resultado = resultado.filter(
        (p) =>
          p.nome.toLowerCase().includes(termoBusca) ||
          (p.descricao && p.descricao.toLowerCase().includes(termoBusca)) ||
          p.categoria.toLowerCase().includes(termoBusca)
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

  // Salvar produtos no localStorage
  function salvarProdutosLocalStorage() {
    if (restauranteAtual) {
      localStorage.setItem(
        `produtos_restaurante_${restauranteAtual.id}`,
        JSON.stringify(produtos)
      );
    }
  }

  // Preview de imagem
  const inputImagemProduto = document.getElementById("input-imagem-produto");
  const previewImg = document.getElementById("preview-img");
  const previewPlaceholder = document.getElementById("preview-placeholder");
  const btnRemoverImagem = document.getElementById("btn-remover-imagem");

  let imagemAtual = null;
  let imagemParaUpload = null;

  if (inputImagemProduto) {
    inputImagemProduto.addEventListener("change", (e) => {
      const file = e.target.files[0];
      if (!file) return;

      // Validar tipo
      if (!file.type.startsWith("image/")) {
        alert("Por favor, selecione uma imagem válida");
        return;
      }

      // Validar tamanho (5MB)
      if (file.size > 5 * 1024 * 1024) {
        alert("Imagem muito grande. Máximo 5MB");
        return;
      }

      imagemParaUpload = file;

      // Mostrar preview
      const reader = new FileReader();
      reader.onload = (e) => {
        previewImg.src = e.target.result;
        previewImg.style.display = "block";
        previewPlaceholder.style.display = "none";
        btnRemoverImagem.style.display = "block";
      };
      reader.readAsDataURL(file);
    });
  }

  // Remover preview de imagem
  if (btnRemoverImagem) {
    btnRemoverImagem.addEventListener("click", () => {
      inputImagemProduto.value = "";
      previewImg.src = "";
      previewImg.style.display = "none";
      previewPlaceholder.style.display = "block";
      btnRemoverImagem.style.display = "none";
      imagemParaUpload = null;
    });
  }

  // Abrir modal para adicionar
  function abrirModalAdicionar() {
    produtoEditando = null;
    imagemAtual = null;
    imagemParaUpload = null;
    modalTitle.textContent = "Adicionar Produto";
    formProduto.reset();
    document.getElementById("produto-ativo").checked = true;

    // Limpar preview
    previewImg.src = "";
    previewImg.style.display = "none";
    previewPlaceholder.style.display = "block";
    btnRemoverImagem.style.display = "none";

    modal.classList.add("active");
  }

  // Salvar produto
  async function salvarProduto(event) {
    event.preventDefault();

    const nome = document.getElementById("produto-nome").value.trim();
    const descricao = document.getElementById("produto-descricao").value.trim();
    const precoStr = document.getElementById("produto-preco").value.trim();
    const categoria = document.getElementById("produto-categoria").value;
    const ativo = document.getElementById("produto-ativo").checked;

    // Converter preço para número
    const preco = converterPrecoParaNumero(precoStr);

    if (!nome || !precoStr || preco <= 0 || isNaN(preco)) {
      alert("Preencha todos os campos obrigatórios corretamente!");
      return;
    }

    try {
      let produtoId;

      if (produtoEditando) {
        // Atualizar produto existente
        const response = await fetch(`${API_URL}/cardapio/atualizar`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            id: produtoEditando.id,
            nome,
            descricao,
            preco,
            categoria,
            imagem: imagemAtual,
            ativo,
          }),
        });

        if (!response.ok) {
          const error = await response.json();
          alert(error.message || "Erro ao atualizar produto");
          return;
        }

        produtoId = produtoEditando.id;
      } else {
        // Adicionar novo produto
        const response = await fetch(`${API_URL}/cardapio/adicionar`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            restauranteId: restauranteAtual.id,
            nome,
            descricao,
            preco,
            categoria,
            imagem: null,
            ativo,
          }),
        });

        if (!response.ok) {
          const error = await response.json();
          alert(error.message || "Erro ao adicionar produto");
          return;
        }

        const data = await response.json();
        produtoId = data.id;
      }

      // Upload de imagem se houver
      if (imagemParaUpload && produtoId) {
        const formData = new FormData();
        formData.append("imagem", imagemParaUpload);

        const uploadResponse = await fetch(
          `${API_URL}/cardapio/upload-imagem/${produtoId}`,
          {
            method: "POST",
            body: formData,
          }
        );

        if (!uploadResponse.ok) {
          console.error("Erro ao fazer upload da imagem");
        }
      }

      alert(
        produtoEditando
          ? "Produto atualizado com sucesso!"
          : "Produto adicionado com sucesso!"
      );
      await carregarProdutos();
      fecharModal();
    } catch (error) {
      console.error("Erro:", error);
      alert("Erro ao salvar produto");
    }
  }

  // Editar produto
  window.editarProduto = function (id) {
    produtoEditando = produtos.find((p) => p.id === id);
    if (!produtoEditando) return;

    modalTitle.textContent = "Editar Produto";
    document.getElementById("produto-nome").value = produtoEditando.nome;
    document.getElementById("produto-descricao").value =
      produtoEditando.descricao || "";
    document.getElementById("produto-preco").value = formatarPreco(
      produtoEditando.preco
    );
    document.getElementById("produto-categoria").value =
      produtoEditando.categoria || "Outros";
    document.getElementById("produto-ativo").checked = produtoEditando.ativo;

    // Mostrar imagem existente
    imagemAtual = produtoEditando.imagem;
    imagemParaUpload = null;

    if (produtoEditando.imagem) {
      previewImg.src = `http://localhost:8080/uploads/produtos/${produtoEditando.imagem}`;
      previewImg.style.display = "block";
      previewPlaceholder.style.display = "none";
      btnRemoverImagem.style.display = "block";
    } else {
      previewImg.src = "";
      previewImg.style.display = "none";
      previewPlaceholder.style.display = "block";
      btnRemoverImagem.style.display = "none";
    }

    modal.classList.add("active");
  };

  // Deletar produto
  window.deletarProduto = async function (id) {
    if (!confirm("Tem certeza que deseja excluir este produto?")) return;

    try {
      const response = await fetch(`${API_URL}/cardapio/${id}`, {
        method: "DELETE",
      });

      if (response.ok) {
        alert("Produto excluído com sucesso!");
        await carregarProdutos();
      } else {
        const error = await response.json();
        alert(error.message || "Erro ao excluir produto");
      }
    } catch (error) {
      console.error("Erro:", error);
      alert("Erro ao excluir produto");
    }
  };

  // Fechar modal
  function fecharModal() {
    modal.classList.remove("active");
    produtoEditando = null;
  }

  // Event Listeners
  if (btnAdicionarProduto)
    btnAdicionarProduto.addEventListener("click", abrirModalAdicionar);
  if (modalClose) modalClose.addEventListener("click", fecharModal);
  if (btnCancelar) btnCancelar.addEventListener("click", fecharModal);
  if (formProduto) formProduto.addEventListener("submit", salvarProduto);
  if (searchInput) searchInput.addEventListener("input", renderizarProdutos);
  if (filterStatus) filterStatus.addEventListener("change", renderizarProdutos);

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
