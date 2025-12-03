const API_URL = "http://localhost:8080/api";

const formCadastro = document.getElementById("form-cadastro-restaurante");

if (formCadastro) {
  formCadastro.addEventListener("submit", async (e) => {
    e.preventDefault();

    const dadosRestaurante = {
      nomeFantasia: document.getElementById("nome").value.trim(),
      nomeProprietario: document.getElementById("proprietario").value.trim(),
      email: document.getElementById("email").value.trim(),
      senha: document.getElementById("senha").value,
      confirmarSenha: document.getElementById("confirmar-senha").value,
      telefone: document.getElementById("telefone").value.trim(),
      cnpj: document.getElementById("cnpj").value.trim(),
      endereco: document.getElementById("endereco").value.trim(),
      dadosBancarios:
        document.getElementById("dados-bancarios")?.value.trim() || "",
    };

    console.log("Enviando dados:", dadosRestaurante);

    try {
      const response = await fetch(`${API_URL}/restaurantes/cadastrar`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(dadosRestaurante),
      });

      const data = await response.json();

      if (response.ok) {
        alert("Restaurante cadastrado com sucesso!");
        console.log("Resposta:", data);

        // Limpar formulário
        formCadastro.reset();

        // Redirecionar ou fazer login automático
        if (confirm("Deseja fazer login agora?")) {
          window.location.href = "loginRestaurante.html";
        }
      } else {
        alert("Erro ao cadastrar: " + (data.message || "Erro desconhecido"));
        console.error("Erro:", data);
      }
    } catch (error) {
      console.error("Erro na requisição:", error);
      alert("Erro ao conectar com o servidor. Tente novamente.");
    }
  });
}
