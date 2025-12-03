package com.foodly.Controller.Restaurante;

import com.foodly.DAO.Restaurante.RestauranteDAO;
import com.foodly.DAO.UsuarioDAO;
import com.foodly.Models.Restaurante.Restaurante;
import com.foodly.Models.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
public class RestauranteController {

    private final UsuarioDAO usuarioDAO;
    private final RestauranteDAO restauranteDAO;

    public RestauranteController() {
        this.usuarioDAO = new UsuarioDAO();
        this.restauranteDAO = new RestauranteDAO();
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarRestaurante(@RequestBody RestauranteRequestDTO request) {
        try {
            System.out.println("=== Cadastro Restaurante ===");
            System.out.println("Nome: " + request.getNomeProprietario());
            
            // Validações
            if (request.getNomeProprietario() == null || request.getNomeProprietario().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Nome do proprietário é obrigatório"));
            }
            
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Email é obrigatório"));
            }
            
            if (request.getSenha() == null || request.getSenha().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Senha é obrigatória"));
            }
            
            if (!request.getSenha().equals(request.getConfirmarSenha())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("As senhas não coincidem"));
            }

            if (request.getNomeFantasia() == null || request.getNomeFantasia().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Nome fantasia é obrigatório"));
            }

            if (request.getCnpj() == null || request.getCnpj().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("CNPJ é obrigatório"));
            }

            if (request.getEndereco() == null || request.getEndereco().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Endereço é obrigatório"));
            }

            // Criar usuário
            Usuario usuario = new Usuario();
            usuario.setNome(request.getNomeProprietario());
            usuario.setEmail(request.getEmail());
            usuario.setSenhaHash(request.getSenha());
            usuario.setTelefone(request.getTelefone());
            usuario.setTipoUsuario("restaurante");
            usuario.setCriadoEm(LocalDateTime.now());

            int usuarioId = usuarioDAO.salvar(usuario);

            // Criar restaurante
            Restaurante restaurante = new Restaurante();
            restaurante.setUsuarioId(usuarioId);
            restaurante.setNomeFantasia(request.getNomeFantasia());
            restaurante.setCnpj(request.getCnpj());
            restaurante.setEndereco(request.getEndereco());
            restaurante.setDadosBancarios(request.getDadosBancarios() != null ? request.getDadosBancarios() : "");
            restaurante.setAtivo(true);

            int restauranteId = restauranteDAO.salvar(restaurante);
            restaurante.setId(restauranteId);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RestauranteResponseDTO(restauranteId, usuarioId, request.getNomeFantasia(), request.getEmail()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listarRestaurantes() {
        try {
            List<Restaurante> restaurantes = restauranteDAO.listarTodos();
            return ResponseEntity.ok(restaurantes);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao listar restaurantes: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarRestaurantePorId(@PathVariable int id) {
        try {
            Restaurante restaurante = restauranteDAO.buscarPorId(id);
            if (restaurante == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(restaurante);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao buscar restaurante: " + e.getMessage()));
        }
    }

    @GetMapping("/visualizar")
    public ResponseEntity<String> visualizarRestaurantes() {
        try {
            List<Restaurante> restaurantes = restauranteDAO.listarTodos();
            
            StringBuilder html = new StringBuilder();
            html.append("""
                    <!DOCTYPE html>
                    <html lang="pt-BR">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Restaurantes Cadastrados - Foodly</title>
                        <link rel="icon" type="image/png" href="/assets/favicon2.png">
                        <style>
                            * { margin: 0; padding: 0; box-sizing: border-box; }
                            body {
                                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                background: #D3D3D3;
                                padding: 20px;
                            }
                            .container {
                                max-width: 1400px;
                                margin: 0 auto;
                                background: white;
                                border-radius: 20px;
                                padding: 40px;
                                box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                            }
                            h1 {
                                color: #ff8a1f;
                                text-align: center;
                                margin-bottom: 30px;
                            }
                            .stats {
                                background: #fff7f0;
                                padding: 20px;
                                border-radius: 10px;
                                margin-bottom: 30px;
                                text-align: center;
                            }
                            .stats h2 {
                                color: #ff4b2b;
                                font-size: 24px;
                            }
                            table {
                                width: 100%;
                                border-collapse: separate;
                                border-spacing: 0;
                                margin-bottom: 20px;
                            }
                            thead th {
                                background: #ff8a1f;
                                color: white;
                                padding: 15px;
                                text-align: left;
                                font-weight: 600;
                            }
                            thead th:first-child {
                                border-top-left-radius: 10px;
                            }
                            thead th:last-child {
                                border-top-right-radius: 10px;
                            }
                            td {
                                padding: 12px 15px;
                                border-bottom: 1px solid #dee2e6;
                            }
                            tr:hover {
                                background: #fff7f0;
                            }
                            .status-badge {
                                padding: 4px 12px;
                                border-radius: 12px;
                                font-size: 12px;
                                font-weight: 600;
                            }
                            .status-ativo {
                                background: #d4edda;
                                color: #155724;
                            }
                            .status-inativo {
                                background: #f8d7da;
                                color: #721c24;
                            }
                            .no-data {
                                padding: 40px;
                            }

                            .no-data p {
                                text-align: center;
                                color: #6c757d;
                                font-size: 24px;
                            }
                            .back-btn, .delete-all-btn {
                                display: inline-block;
                                background: #ff8a1f;
                                color: white;
                                padding: 12px 30px;
                                border-radius: 8px;
                                text-decoration: none;
                                margin-top: 20px;
                                transition: all 0.3s ease;
                            }
                            .back-btn:hover {
                                background: #ff4b2b;
                                transform: translateY(-2px);
                            }
                            .delete-all-btn {
                                background: #dc3545;
                                margin-left: 10px;
                                border: none;
                                cursor: pointer;
                                font-size: 16px;
                            }
                            .delete-all-btn:hover {
                                background: #c82333;
                                transform: translateY(-2px);
                            }
                        </style>
                        <script>
                            async function deletarTodos() {
                                if (!confirm('⚠️ ATENÇÃO! Isso irá deletar TODOS os restaurantes do banco de dados. Deseja continuar?')) {
                                    return;
                                }
                                
                                if (!confirm('Tem certeza ABSOLUTA? Esta ação não pode ser desfeita!')) {
                                    return;
                                }

                                try {
                                    const response = await fetch('/api/restaurantes/deletar/todos', {
                                        method: 'DELETE'
                                    });
                                    
                                    const data = await response.json();
                                    alert(data.message);
                                    window.location.reload();
                                } catch (error) {
                                    alert('Erro ao deletar restaurantes: ' + error.message);
                                }
                            }
                        </script>
                    </head>
                    <body>
                        <div class="container">
                            <h1>🍽 Restaurantes Cadastrados</h1>
                            <div class="stats">
                                <h2>Total:&nbsp;""").append(restaurantes.size()).append("""
                                 Restaurante(s)</h2>
                            </div>
                    """);
            
            if (restaurantes.isEmpty()) {
                html.append("""
                            <div class="no-data" 
                            style="
                            display: flex; 
                            justify-content: center;
                            align-items: center;
                            gap: 5px;">

                            <img src="/assets/close.svg" alt="Close-Icon" style="width:30px;">
                            <p style="align-self:flex-end;">Nenhum cliente cadastrado ainda<p>
                            </div>
                        """);
            } else {
                html.append("""
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Estabelecimento</th>
                                        <th>Proprietário</th>
                                        <th>CNPJ</th>
                                        <th>Email</th>
                                        <th>Telefone</th>
                                        <th>Endereço</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                        """);
                
                for (Restaurante restaurante : restaurantes) {
                    Usuario usuario = usuarioDAO.buscarPorId(restaurante.getUsuarioId());
                    String statusClass = restaurante.isAtivo() ? "status-ativo" : "status-inativo";
                    String statusText = restaurante.isAtivo() ? "Ativo" : "Inativo";
                    
                    html.append("<tr>")
                        .append("<td>").append(restaurante.getId()).append("</td>")
                        .append("<td><strong>").append(restaurante.getNomeFantasia()).append("</strong></td>")
                        .append("<td>").append(usuario != null ? usuario.getNome() : "N/A").append("</td>")
                        .append("<td>").append(restaurante.getCnpj()).append("</td>")
                        .append("<td>").append(usuario != null ? usuario.getEmail() : "N/A").append("</td>")
                        .append("<td>").append(usuario != null && usuario.getTelefone() != null ? usuario.getTelefone() : "-").append("</td>")
                        .append("<td>").append(restaurante.getEndereco()).append("</td>")
                        .append("<td><span class='status-badge ").append(statusClass).append("'>").append(statusText).append("</span></td>")
                        .append("</tr>");
                }
                
                html.append("""
                                </tbody>
                            </table>
                        """);
            }
            
            html.append("""
                            <div style="display: flex; 
                            justify-content: flex-start;">
                            <a href="/" class="back-btn" style="
                            display: flex; 
                            justify-content: center;
                            align-items: center;
                            gap: 5px;">
                            <img src="/assets/return.svg" alt="Return-Icon" style="width:20px;">
                            <p style="align-self:flex-end;"> Voltar para Home</p></a>
                            <button onclick="deletarTodos()" class="delete-all-btn" 
                            style="
                            display: flex; 
                            justify-content: center;
                            align-items: center;
                            gap: 5px;">
                            <img src="/assets/delete.svg" alt="Delete-Icon" style="width:20px;">
                            <p style="align-self:flex-end;">Apagar Todos os Restaurantes<p></button>
                            </div>
                        </div>
                    </body>
                    </html>
                    """);
            
            return ResponseEntity.ok()
                    .contentType(Objects.requireNonNull(MediaType.TEXT_HTML))
                    .body(html.toString());
                    
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(Objects.requireNonNull(MediaType.TEXT_HTML))
                    .body("<h1>Erro ao carregar restaurantes: " + e.getMessage() + "</h1>");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerRestaurante(@PathVariable int id) {
        try {
            Restaurante restaurante = restauranteDAO.buscarPorId(id);
            if (restaurante == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Restaurante não encontrado"));
            }

            int usuarioId = restaurante.getUsuarioId();

            restauranteDAO.deletar(id);
            usuarioDAO.deletar(usuarioId);

            return ResponseEntity.ok(new SuccessResponse("Restaurante removido com sucesso"));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao remover restaurante: " + e.getMessage()));
        }
    }

    @DeleteMapping("/deletar/todos")
    public ResponseEntity<?> deletarTodosRestaurantes() {
        try {
            List<Restaurante> restaurantes = restauranteDAO.listarTodos();
            
            if (restaurantes.isEmpty()) {
                return ResponseEntity.ok(new SuccessResponse("Nenhum restaurante para deletar"));
            }

            int deletados = 0;
            
            for (Restaurante restaurante : restaurantes) {
                try {
                    int usuarioId = restaurante.getUsuarioId();
                    
                    restauranteDAO.deletar(restaurante.getId());
                    usuarioDAO.deletar(usuarioId);
                    deletados++;
                } catch (SQLException e) {
                    System.err.println("Erro ao deletar restaurante ID " + restaurante.getId() + ": " + e.getMessage());
                }
            }

            // Resetar o ID do Restaurante das tabelas, caso tenha sido deletado
            try {
                restauranteDAO.resetarAutoIncrement();
                usuarioDAO.resetarAutoIncrement();
            } catch (SQLException e) {
                System.err.println("Erro ao resetar AUTO_INCREMENT: " + e.getMessage());
            }

            return ResponseEntity.ok(new SuccessResponse(deletados + " restaurante(s) deletado(s) com sucesso. IDs resetados."));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao deletar restaurantes: " + e.getMessage()));
        }
    }

    // DTOs
    static class RestauranteRequestDTO {
        private String nomeProprietario;
        private String email;
        private String senha;
        private String confirmarSenha;
        private String telefone;
        private String nomeFantasia;
        private String cnpj;
        private String endereco;
        private String dadosBancarios;

        public String getNomeProprietario() { return nomeProprietario; }
        public void setNomeProprietario(String nomeProprietario) { this.nomeProprietario = nomeProprietario; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
        public String getConfirmarSenha() { return confirmarSenha; }
        public void setConfirmarSenha(String confirmarSenha) { this.confirmarSenha = confirmarSenha; }
        public String getTelefone() { return telefone; }
        public void setTelefone(String telefone) { this.telefone = telefone; }
        public String getNomeFantasia() { return nomeFantasia; }
        public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
        public String getCnpj() { return cnpj; }
        public void setCnpj(String cnpj) { this.cnpj = cnpj; }
        public String getEndereco() { return endereco; }
        public void setEndereco(String endereco) { this.endereco = endereco; }
        public String getDadosBancarios() { return dadosBancarios; }
        public void setDadosBancarios(String dadosBancarios) { this.dadosBancarios = dadosBancarios; }
    }

    static class RestauranteResponseDTO {
        private int restauranteId;
        private int usuarioId;
        private String nomeFantasia;
        private String email;

        public RestauranteResponseDTO(int restauranteId, int usuarioId, String nomeFantasia, String email) {
            this.restauranteId = restauranteId;
            this.usuarioId = usuarioId;
            this.nomeFantasia = nomeFantasia;
            this.email = email;
        }

        public int getRestauranteId() { return restauranteId; }
        public int getUsuarioId() { return usuarioId; }
        public String getNomeFantasia() { return nomeFantasia; }
        public String getEmail() { return email; }
    }

    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }

    static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }
}
