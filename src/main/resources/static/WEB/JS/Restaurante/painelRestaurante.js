if (window.location.pathname.includes("painelRestaurante.html")) {
  const API_URL = "http://localhost:8080/api";

  // Elementos do DOM
  const restauranteNome = document.getElementById("restaurante-nome");
  const restauranteCnpj = document.getElementById("restaurante-cnpj");
  const restauranteEndereco = document.getElementById("restaurante-endereco");
  const restauranteStatus = document.getElementById("restaurante-status");
  const proprietarioNome = document.getElementById("proprietario-nome");
  const proprietarioEmail = document.getElementById("proprietario-email");
  const proprietarioTelefone = document.getElementById("proprietario-telefone");
  const avatarInicial = document.getElementById("restaurante-avatar-inicial");
  const avatarImg = document.getElementById("restaurante-avatar");
  const inputFoto = document.getElementById("input-foto");
  const btnRemoverFoto = document.getElementById("btn-remover-foto");

  const btnEditarRestaurante = document.getElementById(
    "btn-editar-restaurante"
  );
  const btnLogout = document.getElementById("btn-logout");
  const modal = document.getElementById("modal-editar");
  const modalClose = document.getElementById("modal-close");
  const btnCancelar = document.getElementById("btn-cancelar");
  const formEditarRestaurante = document.getElementById(
    "form-editar-restaurante"
  );

  // Campos do formulário
  const editNomeFantasia = document.getElementById("edit-nome-fantasia");
  const editEndereco = document.getElementById("edit-endereco");
  const editTelefone = document.getElementById("edit-telefone");
  const editDadosBancarios = document.getElementById("edit-dados-bancarios");

  let usuarioAtual = null;
  let restauranteAtual = null;

  // Carregar dados do restaurante
  async function carregarDadosRestaurante() {
    try {
      const usuarioJson = localStorage.getItem("usuario");
      console.log("Dados do localStorage:", usuarioJson);

      if (!usuarioJson) {
        console.error("Nenhum usuário encontrado no localStorage");
        alert("Sessão expirada. Faça login novamente.");
        window.location.href = "../../html/escolhaPerfil.html";
        return;
      }

      usuarioAtual = JSON.parse(usuarioJson);
      console.log("Usuário parseado:", usuarioAtual);

      // Verificar se é restaurante
      if (usuarioAtual.tipoUsuario !== "restaurante") {
        alert("Acesso negado. Este painel é apenas para restaurantes.");
        window.location.href = "../../html/escolhaPerfil.html";        return;
      }

      // Buscar dados do restaurante
      const response = await fetch(
        `${API_URL}/auth/perfil/${usuarioAtual.usuarioId}`
      );

      if (response.ok) {
        const dadosAtualizados = await response.json();
        console.log("Dados atualizados:", dadosAtualizados);
        usuarioAtual = dadosAtualizados;
        localStorage.setItem("usuario", JSON.stringify(dadosAtualizados));
      }

      // Buscar lista de restaurantes para encontrar o restaurante deste usuário
      const restaurantesResponse = await fetch(`${API_URL}/restaurantes`);
      if (restaurantesResponse.ok) {
        const restaurantes = await restaurantesResponse.json();
        restauranteAtual = restaurantes.find(
          (r) => r.usuarioId === usuarioAtual.usuarioId
        );

        if (restauranteAtual) {
          exibirDadosRestaurante();
          await carregarFotoRestaurante(usuarioAtual.usuarioId);
        } else {
          console.error("Restaurante não encontrado");
          alert("Erro: Restaurante não encontrado no sistema");
        }
      }
    } catch (error) {
      console.error("Erro ao carregar dados:", error);
      alert("Erro ao carregar dados do restaurante");
    }
  }

  // Exibir dados do restaurante
  function exibirDadosRestaurante() {
    if (!restauranteAtual || !usuarioAtual) return;

    // Dados do restaurante
    if (restauranteNome)
      restauranteNome.textContent =
        restauranteAtual.nomeFantasia || "Não informado";
    if (restauranteCnpj)
      restauranteCnpj.textContent = restauranteAtual.cnpj || "Não informado";
    if (restauranteEndereco)
      restauranteEndereco.textContent =
        restauranteAtual.endereco || "Não informado";
    if (restauranteStatus) {
      const status = restauranteAtual.ativo ? "✅ Ativo" : "❌ Inativo";
      restauranteStatus.textContent = status;
      restauranteStatus.style.color = restauranteAtual.ativo
        ? "#16a34a"
        : "#dc2626";
    }

    // Dados do proprietário
    if (proprietarioNome)
      proprietarioNome.textContent = usuarioAtual.nome || "Não informado";
    if (proprietarioEmail)
      proprietarioEmail.textContent = usuarioAtual.email || "Não informado";
    if (proprietarioTelefone)
      proprietarioTelefone.textContent =
        usuarioAtual.telefone || "Não informado";

    // Avatar com inicial
    if (avatarInicial && restauranteAtual.nomeFantasia) {
      const inicial = restauranteAtual.nomeFantasia.charAt(0).toUpperCase();
      avatarInicial.textContent = inicial;
    }

    // Mostrar foto se existir
    if (usuarioAtual.fotoPerfil) {
      if (avatarImg) {
        avatarImg.src = `http://localhost:8080/uploads/fotos-perfil/${usuarioAtual.fotoPerfil}`;
        avatarImg.style.display = "block";
      }
      if (avatarInicial) {
        avatarInicial.style.display = "none";
      }
      if (btnRemoverFoto) {
        btnRemoverFoto.style.display = "flex";
      }
    } else {
      if (btnRemoverFoto) {
        btnRemoverFoto.style.display = "none";
      }
    }
  }

  // Upload de foto
  if (inputFoto) {
    inputFoto.addEventListener("change", async (e) => {
      const file = e.target.files[0];
      if (!file) return;

      if (!usuarioAtual || !usuarioAtual.usuarioId) {
        alert("Erro: usuário não identificado");
        return;
      }

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

      const formData = new FormData();
      formData.append("foto", file);

      try {
        const response = await fetch(
          `${API_URL}/clientes/upload-foto/${usuarioAtual.usuarioId}`,
          {
            method: "POST",
            body: formData,
          }
        );

        if (response.ok) {
          const data = await response.json();
          console.log("Upload bem-sucedido:", data);

          // Atualizar a imagem imediatamente
          if (avatarImg) {
            avatarImg.src = `http://localhost:8080${
              data.url
            }?t=${new Date().getTime()}`;
            avatarImg.style.display = "block";
          }
          if (avatarInicial) {
            avatarInicial.style.display = "none";
          }

          // Buscar dados atualizados do servidor
          const perfilResponse = await fetch(
            `${API_URL}/auth/perfil/${usuarioAtual.usuarioId}`
          );
          if (perfilResponse.ok) {
            const perfilAtualizado = await perfilResponse.json();
            console.log("Perfil atualizado:", perfilAtualizado);
            usuarioAtual = perfilAtualizado;
            localStorage.setItem("usuario", JSON.stringify(perfilAtualizado));
            localStorage.setItem(
              "usuarioLogado",
              JSON.stringify(perfilAtualizado)
            );

            // Mostrar botão de remover
            if (btnRemoverFoto) {
              btnRemoverFoto.style.display = "flex";
            }
          }

          alert("Logo atualizado com sucesso!");
        } else {
          const error = await response.json();
          alert(error.message || "Erro ao fazer upload");
        }
      } catch (error) {
        console.error("Erro:", error);
        alert("Erro ao fazer upload da foto");
      }
    });
  }

  // Carregar foto existente
  async function carregarFotoRestaurante(usuarioId) {
    try {
      const response = await fetch(`${API_URL}/auth/perfil/${usuarioId}`);
      if (response.ok) {
        const usuario = await response.json();
        console.log("Foto perfil do restaurante:", usuario.fotoPerfil);

        if (
          usuario.fotoPerfil &&
          usuario.fotoPerfil !== null &&
          usuario.fotoPerfil !== ""
        ) {
          const fotoUrl = `http://localhost:8080/uploads/fotos-perfil/${
            usuario.fotoPerfil
          }?t=${new Date().getTime()}`;
          console.log("URL da foto:", fotoUrl);

          if (avatarImg) {
            avatarImg.src = fotoUrl;
            avatarImg.style.display = "block";
            avatarImg.onerror = function () {
              console.error("Erro ao carregar imagem:", this.src);
              this.style.display = "none";
              if (avatarInicial) avatarInicial.style.display = "flex";
            };
          }
          if (avatarInicial) {
            avatarInicial.style.display = "none";
          }
          if (btnRemoverFoto) {
            btnRemoverFoto.style.display = "flex";
          }
        } else {
          console.log("Restaurante não possui logo");
          if (avatarImg) avatarImg.style.display = "none";
          if (avatarInicial) avatarInicial.style.display = "flex";
          if (btnRemoverFoto) btnRemoverFoto.style.display = "none";
        }
      }
    } catch (error) {
      console.error("Erro ao carregar foto:", error);
    }
  }

  // Remover foto
  async function removerFotoRestaurante() {
    if (!usuarioAtual || !usuarioAtual.usuarioId) {
      alert("Erro: usuário não identificado");
      return;
    }

    if (!confirm("Tem certeza que deseja remover o logo do restaurante?")) {
      return;
    }

    try {
      const response = await fetch(
        `${API_URL}/clientes/remover-foto/${usuarioAtual.usuarioId}`,
        {
          method: "DELETE",
        }
      );

      if (response.ok) {
        // Esconder imagem e mostrar inicial
        if (avatarImg) {
          avatarImg.style.display = "none";
          avatarImg.src = "";
        }
        if (avatarInicial) {
          avatarInicial.style.display = "flex";
        }
        if (btnRemoverFoto) {
          btnRemoverFoto.style.display = "none";
        }

        // Atualizar dados no localStorage
        const perfilResponse = await fetch(
          `${API_URL}/auth/perfil/${usuarioAtual.usuarioId}`
        );
        if (perfilResponse.ok) {
          const perfilAtualizado = await perfilResponse.json();
          usuarioAtual = perfilAtualizado;
          localStorage.setItem("usuario", JSON.stringify(perfilAtualizado));
          localStorage.setItem(
            "usuarioLogado",
            JSON.stringify(perfilAtualizado)
          );
        }

        alert("Logo removido com sucesso!");
      } else {
        const error = await response.json();
        alert(error.message || "Erro ao remover logo");
      }
    } catch (error) {
      console.error("Erro:", error);
      alert("Erro ao remover logo");
    }
  }

  // Abrir modal de edição
  function abrirModal() {
    if (!restauranteAtual || !usuarioAtual) {
      alert("Erro: dados não carregados");
      return;
    }

    editNomeFantasia.value = restauranteAtual.nomeFantasia || "";
    editEndereco.value = restauranteAtual.endereco || "";
    editTelefone.value = usuarioAtual.telefone || "";
    editDadosBancarios.value = restauranteAtual.dadosBancarios || "";

    modal.classList.add("active");
  }

  // Fechar modal
  function fecharModal() {
    modal.classList.remove("active");
  }

  // Salvar alterações
  async function salvarRestaurante(event) {
    event.preventDefault();

    try {
      // Aqui você implementaria a chamada para atualizar o restaurante
      // Por enquanto, apenas mostra uma mensagem
      alert("Funcionalidade de edição será implementada em breve!");
      fecharModal();
    } catch (error) {
      console.error("Erro ao atualizar:", error);
      alert("Erro ao atualizar dados: " + error.message);
    }
  }

  // Logout
  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("usuario");
    localStorage.removeItem("usuarioLogado");
    window.location.href = "../../html/escolhaPerfil.html";  }

  // Event Listeners
  if (btnEditarRestaurante)
    btnEditarRestaurante.addEventListener("click", abrirModal);
  if (modalClose) modalClose.addEventListener("click", fecharModal);
  if (btnCancelar) btnCancelar.addEventListener("click", fecharModal);
  if (formEditarRestaurante)
    formEditarRestaurante.addEventListener("submit", salvarRestaurante);
  if (btnLogout) btnLogout.addEventListener("click", logout);
  if (btnRemoverFoto)
    btnRemoverFoto.addEventListener("click", removerFotoRestaurante);

  // Fechar modal ao clicar fora
  if (modal) {
    modal.addEventListener("click", (e) => {
      if (e.target === modal) {
        fecharModal();
      }
    });
  }

  // Inicializar quando o DOM estiver pronto
  document.addEventListener("DOMContentLoaded", () => {
    carregarDadosRestaurante();
  });
}
