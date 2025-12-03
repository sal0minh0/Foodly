// Utilitário de autenticação para usar em todas as páginas

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
  localStorage.removeItem("usuarioLogado");
  localStorage.removeItem("token");
  localStorage.removeItem("usuario");
  window.location.href = "../escolhaPerfil.html";
}

function verificarAutenticacao() {
  // Não redirecionar se estiver na página de login ou cadastro
  const paginasPublicas = [
    "index.html",
    "cadastro.html",
    "cadastroCliente.html",
    "cadastroRestaurante.html",
    "escolhaPerfil.html",
    "loginCliente.html",
    "loginRestaurante.html",
  ];
  const paginaAtual = window.location.pathname.split("/").pop();

  if (paginasPublicas.includes(paginaAtual)) {
    return;
  }

  if (!isLogado()) {
    alert("Você precisa estar logado para acessar esta página");
    window.location.href = "../escolhaPerfil.html";
  }
}
