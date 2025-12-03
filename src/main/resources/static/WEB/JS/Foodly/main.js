// main.js
// Aqui ficam os comportamentos globais do seu projeto Foodly / FoodCart 😊

// -----------------------------------------------------
// 1) LOGIN: redirecionar para o menu após o envio
// -----------------------------------------------------
const loginForm = document.querySelector("#form-login");

if (loginForm) {
  loginForm.addEventListener("submit", function (event) {
    event.preventDefault(); // impede o reload da página

    window.location.href = "../../html/Foodly/menuPrincipal.html";
  });
}

// -----------------------------------------------------
// 2) BOTÕES "VER CARDÁPIO" (página de restaurantes)
// -----------------------------------------------------
const botoesCardapio = document.querySelectorAll(".btn-cardapio");

if (botoesCardapio.length > 0) {
  botoesCardapio.forEach((botao) => {
    botao.addEventListener("click", () => {
      // No futuro você pode passar o ID do restaurante, etc.
      // Por enquanto, leva para o menu principal.
      window.location.href = "../../html/Foodly/menuPrincipal.html";
    });
  });
}

// -----------------------------------------------------
// 3) FORMULÁRIO DE SUPORTE (support.html / suporte.html)
// -----------------------------------------------------
// Espera um <form class="support-form"> com campos #email e #mensagem
const suporteForm = document.querySelector(".support-form");

if (suporteForm) {
  suporteForm.addEventListener("submit", function (event) {
    const email = suporteForm.querySelector("#email");
    const mensagem = suporteForm.querySelector("#mensagem");

    if (!email.value.trim() || !mensagem.value.trim()) {
      event.preventDefault();
      alert(
        "Por favor, preencha pelo menos o e-mail e a descrição do problema."
      );
      return;
    }

    event.preventDefault(); // só para não recarregar a página
    alert("Mensagem enviada com sucesso! Nossa equipe\no retornará em breve.");
    suporteForm.reset();
  });
}

// -----------------------------------------------------
// 4) CADASTRO DE RESTAURANTE
//    - Impede o reload da página
//    - Faz uma validação simples
//    - Mostra mensagem de sucesso
//    - Limpa o formulário
// -----------------------------------------------------
const cadastroRestauranteForm = document.querySelector(
  "#form-cadastro-restaurante"
);

if (cadastroRestauranteForm) {
  cadastroRestauranteForm.addEventListener("submit", function (event) {
    event.preventDefault(); // impede o submit tradicional (reload)

    // Pegando os campos principais
    const nome = cadastroRestauranteForm.querySelector("#nome");
    const categoria = cadastroRestauranteForm.querySelector("#categoria");
    const cidade = cadastroRestauranteForm.querySelector("#cidade");
    const bairro = cadastroRestauranteForm.querySelector("#bairro");

    // Validação bem simples
    if (
      !nome.value.trim() ||
      !categoria.value ||
      !cidade.value.trim() ||
      !bairro.value.trim()
    ) {
      alert("Por favor, preencha pelo menos Nome, Categoria, Cidade e Bairro.");
      return;
    }

    // Se passou pela validação:
    alert("Restaurante cadastrado com sucesso! 🎉");

    // Limpar todos os campos
    cadastroRestauranteForm.reset();
  });
}

// Arquivo principal de utilitários
console.log("Foodly - Sistema carregado");

// Arquivo auxiliar - funções compartilhadas entre páginas

// Funções de autenticação
function getUsuarioLogado() {
  const usuario = localStorage.getItem("usuarioLogado");
  return usuario ? JSON.parse(usuario) : null;
}

function salvarUsuarioLogado(usuario) {
  localStorage.setItem("usuarioLogado", JSON.stringify(usuario));
}

function isLogado() {
  return getUsuarioLogado() !== null;
}

function logout() {
  if (confirm("Deseja realmente sair?")) {
    localStorage.removeItem("usuarioLogado");
    window.location.href = "../../html/Cliente/loginCliente.html";
  }
}

function verificarAutenticacao() {
  if (!isLogado()) {
    alert("Você precisa estar logado para acessar esta página");
    window.location.href = "../../html/Cliente/loginCliente.html";
  }
}

// Atualizar informações do usuário no header (se houver)
document.addEventListener("DOMContentLoaded", () => {
  const usuario = getUsuarioLogado();

  // Atualizar nome do usuário se elemento existir
  const nomeUsuarioElement = document.getElementById("nome-usuario");
  if (nomeUsuarioElement && usuario) {
    nomeUsuarioElement.textContent = usuario.nome;
  }

  // Atualizar avatar se elemento existir
  const avatarElement = document.getElementById("user-avatar");
  if (avatarElement && usuario && usuario.nome) {
    avatarElement.textContent = usuario.nome.charAt(0).toUpperCase();
  }
});
