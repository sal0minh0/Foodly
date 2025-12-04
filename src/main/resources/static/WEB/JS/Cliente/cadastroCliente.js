const API_BASE_URL = CONFIG.API_URL;

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("form-cadastro-cliente");

  if (form) {
    form.addEventListener("submit", handleCadastro);
  }
});

async function handleCadastro(event) {
  event.preventDefault();

  const formData = new FormData(event.target);
  const data = {
    nome: formData.get("nome"),
    email: formData.get("email"),
    senha: formData.get("senha"),
    confirmarSenha: formData.get("confirmarSenha"),
    telefone: formData.get("telefone"),
    enderecoPadrao: formData.get("enderecoPadrao") || "",
  };

  // Validação básica frontend
  if (data.senha !== data.confirmarSenha) {
    alert("As senhas não coincidem!");
    return;
  }

  console.log("Enviando dados:", data);

  try {
    const response = await fetch(`${API_BASE_URL}/clientes/cadastrar`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });

    console.log("Status da resposta:", response.status);

    if (!response.ok) {
      const result = await response.json();
      console.error("❌ Erro no cadastro:", result);
      alert(result.message || "Erro ao realizar cadastro");
      return;
    }

    const result = await response.json();
    console.log("Resposta do servidor:", result);
    console.log("✅ Cadastro bem-sucedido!");

    alert(`Cadastro realizado com sucesso! ID do cliente: ${result.clienteId}`);

    console.log("🔄 Redirecionando para login...");
    window.location.href = "loginCliente.html";
  } catch (error) {
    console.error("❌ Erro detalhado:", error);
    alert(
      "Erro ao conectar com o servidor. Verifique se o backend está rodando."
    );
  }
}
