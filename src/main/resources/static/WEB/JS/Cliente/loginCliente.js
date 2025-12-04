const API_URL = CONFIG.API_URL;

document.addEventListener("DOMContentLoaded", () => {
  const formLogin = document.getElementById("form-login");

  if (formLogin) {
    formLogin.addEventListener("submit", async (e) => {
      e.preventDefault();

      const email = document.getElementById("email").value.trim();
      const senha = document.getElementById("senha").value;

      if (!email || !senha) {
        alert("Preencha todos os campos!");
        return;
      }

      try {
        const response = await fetch(`${API_URL}/auth/login`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ email, senha }),
        });

        if (response.ok) {
          const usuario = await response.json();
          console.log("Login bem-sucedido:", usuario);

          // Verificar se é cliente
          if (usuario.tipoUsuario === "restaurante") {
            alert("Use o login de restaurante para acessar como restaurante.");
            return;
          }

          // Salvar usuário no localStorage
          localStorage.setItem("usuario", JSON.stringify(usuario));
          localStorage.setItem("usuarioLogado", JSON.stringify(usuario));

          alert(`Bem-vindo, ${usuario.nome}!`);

          // Redirecionar para menuPrincipal.html
          window.location.href = "../../html/Foodly/menuPrincipal.html";
        } else {
          const erro = await response.json();
          alert(erro.message || "Email ou senha incorretos");
        }
      } catch (error) {
        console.error("Erro ao fazer login:", error);
        alert("Erro ao conectar com o servidor");
      }
    });
  }
});
