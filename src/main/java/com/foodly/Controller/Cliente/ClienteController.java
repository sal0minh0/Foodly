package com.foodly.Controller.Cliente;

import com.foodly.DAO.Cliente.ClienteDAO;
import com.foodly.DAO.UsuarioDAO;
import com.foodly.DAO.Cliente.AssinaturaPremiumDAO;
import com.foodly.Models.Cliente.Cliente;
import com.foodly.Models.Usuario;
import com.foodly.Models.Cliente.AssinaturaPremium;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final UsuarioDAO usuarioDAO;
    private final ClienteDAO clienteDAO;
    private final AssinaturaPremiumDAO assinaturaPremiumDAO;
    private static final String UPLOAD_DIR = "uploads/fotos-perfil/";

    public ClienteController() {
        this.usuarioDAO = new UsuarioDAO();
        this.clienteDAO = new ClienteDAO();
        this.assinaturaPremiumDAO = new AssinaturaPremiumDAO();
        
        // Criar diretório de uploads, isso se não existir
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    /**
     * POST /api/clientes/cadastrar
     * DEVE vir ANTES de /{id}
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarCliente(@RequestBody ClienteRequestDTO request) {
        try {
            System.out.println("=== Cadastro Cliente ===");
            System.out.println("Nome: " + request.getNome());
            
            if (request.getNome() == null || request.getNome().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Nome é obrigatório"));
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

            Usuario usuario = new Usuario();
            usuario.setNome(request.getNome());
            usuario.setEmail(request.getEmail());
            usuario.setSenhaHash(request.getSenha()); 
            usuario.setTelefone(request.getTelefone());
            usuario.setTipoUsuario("cliente");
            usuario.setCriadoEm(LocalDateTime.now());

            int usuarioId = usuarioDAO.salvar(usuario);

            Cliente cliente = new Cliente();
            cliente.setUsuarioId(usuarioId);
            cliente.setEnderecoPadrao(request.getEnderecoPadrao() != null ? request.getEnderecoPadrao() : "");

            int clienteId = clienteDAO.salvar(cliente);
            cliente.setId(clienteId);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ClienteResponseDTO(clienteId, usuarioId, request.getNome(), request.getEmail()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listarTodos();
            return ResponseEntity.ok(clientes);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao listar clientes: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarClientePorId(@PathVariable int id) {
        try {
            Cliente cliente = clienteDAO.buscarPorId(id);
            if (cliente == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(cliente);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao buscar cliente: " + e.getMessage()));
        }
    }

    @GetMapping("/visualizar")
    public ResponseEntity<String> visualizarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listarTodos();
            
            StringBuilder html = new StringBuilder();
            html.append("""
                    <!DOCTYPE html>
                    <html lang="pt-BR">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Clientes Cadastrados - Foodly</title>
                        <link rel="icon" type="image/png" href="/assets/favicon2.png">
                        <style>
                            * { margin: 0; padding: 0; box-sizing: border-box; }
                            body {
                                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                background: #D3D3D3;
                                padding: 20px;
                            }
                            .container {
                                max-width: 1200px;
                                margin: 0 auto;
                                background: white;
                                border-radius: 20px;
                                padding: 40px;
                                box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                            }
                            h1 {
                                color: #667eea;
                                text-align: center;
                                margin-bottom: 30px;
                            }
                            .stats {
                                background: #f8f9fa;
                                padding: 20px;
                                border-radius: 10px;
                                margin-bottom: 30px;
                                text-align: center;
                            }
                            .stats h2 {
                                color: #28a745;
                                font-size: 24px;
                            }
                            table {
                                width: 100%;
                                border-collapse: separate;
                                border-spacing: 0;      
                                margin-bottom: 20px;
                            }

                            thead th {
                                background: #667eea;
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
                                background: #f8f9fa;
                            }
                            
                            .premium-badge {
                                background: linear-gradient(135deg, #ffd700, #ffed4e);
                                color: #333;
                                padding: 4px 8px;
                                border-radius: 12px;
                                font-size: 0.75em;
                                font-weight: bold;
                                text-transform: uppercase;
                                box-shadow: 0 2px 4px rgba(255, 215, 0, 0.3);
                            }
                            
                            .basico-badge {
                                background: linear-gradient(135deg, #6c757d, #adb5bd);
                                color: white;
                                padding: 4px 8px;
                                border-radius: 12px;
                                font-size: 0.75em;
                                font-weight: bold;
                                text-transform: uppercase;
                                box-shadow: 0 2px 4px rgba(108, 117, 125, 0.3);
                            }
                            
                            .no-premium {
                                color: #6c757d;
                                font-style: italic;
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
                                background: #667eea;
                                color: white;
                                padding: 12px 30px;
                                border-radius: 8px;
                                text-decoration: none;
                                margin-top: 20px;
                                transition: all 0.3s ease;
                            }
                            .back-btn:hover {
                                background: #1f44e7;
                                transform: translateY(-2px);
                            }
                            .delete-all-btn {
                                display: inline-block;
                                background: #dc3545;
                                color: white;
                                padding: 12px 30px;
                                border-radius: 8px;
                                text-decoration: none;
                                margin-top: 20px;
                                margin-left: 10px;
                                transition: all 0.3s ease;
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
                                if (!confirm('⚠️ ATENÇÃO! Isso irá deletar TODOS os clientes do banco de dados. Deseja continuar?')) {
                                    return;
                                }
                                
                                if (!confirm('Tem certeza ABSOLUTA? Esta ação não pode ser desfeita!')) {
                                    return;
                                }

                                try {
                                    const response = await fetch('/api/clientes/deletar/todos', {
                                        method: 'DELETE'
                                    });
                                    
                                    const data = await response.json();
                                    alert(data.message);
                                    window.location.reload();
                                } catch (error) {
                                    alert('Erro ao deletar clientes: ' + error.message);
                                }
                            }
                        </script>
                    </head>
                    <body>
                        <div class="container">
                            <h1>👥 Clientes Cadastrados</h1>
                            <div class="stats">
                                <h2>Total:&nbsp;""").append(clientes.size()).append("""
                                 Cliente(s)</h2>
                            </div>
                    """);
            
            if (clientes.isEmpty()) {
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
                                        <th>Nome</th>
                                        <th>Email</th>
                                        <th>Telefone</th>
                                        <th>Endereço</th>
                                        <th>Plano</th>
                                    </tr>
                                </thead>
                                <tbody>
                        """);
                
                for (Cliente cliente : clientes) {
                    Usuario usuario = usuarioDAO.buscarPorId(cliente.getUsuarioId());
                    
                    // Verificar se é premium
                    String statusPremium = "<span class='basico-badge'>BÁSICO</span>";
                    try {
                        AssinaturaPremium assinatura = assinaturaPremiumDAO.buscarPorClienteId(cliente.getId());
                        if (assinatura != null && "ativa".equals(assinatura.getStatus())) {
                            statusPremium = "<span class='premium-badge'>PREMIUM</span>";
                        }
                    } catch (SQLException e) {
                        // Se der erro, assume que não é premium
                        statusPremium = "<span class='no-premium'>Erro</span>";
                    }
                    
                    html.append("<tr>")
                        .append("<td>").append(cliente.getId()).append("</td>")
                        .append("<td>").append(usuario != null ? usuario.getNome() : "N/A").append("</td>")
                        .append("<td>").append(usuario != null ? usuario.getEmail() : "N/A").append("</td>")
                        .append("<td>").append(usuario != null && usuario.getTelefone() != null ? usuario.getTelefone() : "-").append("</td>")
                        .append("<td>").append(cliente.getEnderecoPadrao() != null && !cliente.getEnderecoPadrao().isEmpty() ? cliente.getEnderecoPadrao() : "-").append("</td>")
                        .append("<td>").append(statusPremium).append("</td>")
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
                            <p style="align-self:flex-end;">Apagar Todos os Clientes<p></button>
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
                    .body("<h1>Erro ao carregar clientes: " + e.getMessage() + "</h1>");
        }
    }

    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarCliente(@RequestBody AtualizarClienteDTO request) {
        try {
            if (request.getUsuarioId() <= 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("ID do usuário inválido"));
            }

            // Atualizar dados do usuário
            Usuario usuario = usuarioDAO.buscarPorId(request.getUsuarioId());
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }

            usuario.setNome(request.getNome());
            usuario.setEmail(request.getEmail());
            usuario.setTelefone(request.getTelefone());
            
            usuarioDAO.atualizar(usuario);

            // Atualizar endereço do cliente
            if (request.getClienteId() > 0) {
                Cliente cliente = clienteDAO.buscarPorId(request.getClienteId());
                if (cliente != null) {
                    cliente.setEnderecoPadrao(request.getEnderecoPadrao());
                    clienteDAO.atualizar(cliente);
                }
            }

            return ResponseEntity.ok(new SuccessResponse("Perfil atualizado com sucesso"));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao atualizar perfil: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerCliente(@PathVariable int id) {
        try {
            // Buscar o cliente para obter a Id do Usuário
            Cliente cliente = clienteDAO.buscarPorId(id);
            if (cliente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Cliente não encontrado"));
            }

            int usuarioId = cliente.getUsuarioId();

            // Buscar usuário e deletar a foto se existir
            Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
            if (usuario != null && usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isEmpty()) {
                try {
                    Files.deleteIfExists(Paths.get(UPLOAD_DIR + usuario.getFotoPerfil()));
                    System.out.println("Foto deletada: " + usuario.getFotoPerfil());
                } catch (IOException e) {
                    System.err.println("Erro ao deletar foto: " + e.getMessage());
                }
            }

            // Remover o cliente no DAO
            clienteDAO.deletar(id);

            // Remover o usuário no DAO
            usuarioDAO.deletar(usuarioId);

            return ResponseEntity.ok(new SuccessResponse("Cliente removido com sucesso"));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao remover cliente: " + e.getMessage()));
        }
    }

    @DeleteMapping("/deletar/todos")
    public ResponseEntity<?> deletarTodosClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listarTodos();
            
            if (clientes.isEmpty()) {
                return ResponseEntity.ok(new SuccessResponse("Nenhum cliente para deletar"));
            }

            int deletados = 0;
            
            for (Cliente cliente : clientes) {
                try {
                    int usuarioId = cliente.getUsuarioId();
                    
                    // Buscar usuário e deletar foto, isso se existir
                    Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
                    if (usuario != null && usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isEmpty()) {
                        try {
                            Files.deleteIfExists(Paths.get(UPLOAD_DIR + usuario.getFotoPerfil()));
                            System.out.println("Foto deletada: " + usuario.getFotoPerfil());
                        } catch (IOException e) {
                            System.err.println("Erro ao deletar foto do usuário " + usuarioId + ": " + e.getMessage());
                        }
                    }
                    
                    clienteDAO.deletar(cliente.getId());
                    usuarioDAO.deletar(usuarioId);
                    deletados++;
                } catch (SQLException e) {
                    System.err.println("Erro ao deletar cliente ID " + cliente.getId() + ": " + e.getMessage());
                }
            }

            // Resetar Incremento de ID nas tabelas, se deletarmos um cliente
            try {
                clienteDAO.resetarAutoIncrement();
                usuarioDAO.resetarAutoIncrement();
            } catch (SQLException e) {
                System.err.println("Erro ao resetar AUTO_INCREMENT: " + e.getMessage());
            }

            return ResponseEntity.ok(new SuccessResponse(deletados + " cliente(s) deletado(s) com sucesso. IDs resetados."));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao deletar clientes: " + e.getMessage()));
        }
    }

    @PostMapping("/upload-foto/{usuarioId}")
    public ResponseEntity<?> uploadFotoPerfil(
            @PathVariable int usuarioId,
            @RequestParam("foto") MultipartFile file) {
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Arquivo não enviado"));
            }

            // Validar o tipo de arquivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Apenas imagens são permitidas"));
            }

            // Validar tamanho (máximo 5MB, para não ficar pesado o site)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Imagem muito grande. Máximo 5MB"));
            }

            // Validar o nome do arquivo
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Nome do arquivo inválido"));
            }

            // Gerar um nome único para o arquivo
            String extensao = originalFilename.substring(originalFilename.lastIndexOf("."));
            String nomeArquivo = usuarioId + "_" + UUID.randomUUID().toString() + extensao;
            
            // Salvar o arquivo de imagem
            Path path = Paths.get(UPLOAD_DIR + nomeArquivo);
            Files.write(path, file.getBytes());

            // Atualizar usuário no banco
            Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }

            // Deletar foto antiga, depois de adicionar a nova, isso se a antiga existir
            if (usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isEmpty()) {
                try {
                    Files.deleteIfExists(Paths.get(UPLOAD_DIR + usuario.getFotoPerfil()));
                } catch (IOException e) {
                    System.err.println("Erro ao deletar foto antiga: " + e.getMessage());
                }
            }

            usuario.setFotoPerfil(nomeArquivo);
            usuarioDAO.atualizar(usuario);

            return ResponseEntity.ok(new FotoResponse(nomeArquivo, "/uploads/fotos-perfil/" + nomeArquivo));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao fazer upload: " + e.getMessage()));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao atualizar banco de dados: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remover-foto/{usuarioId}")
    public ResponseEntity<?> removerFotoPerfil(@PathVariable int usuarioId) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }

            if (usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isEmpty()) {
                try {
                    Files.deleteIfExists(Paths.get(UPLOAD_DIR + usuario.getFotoPerfil()));
                } catch (IOException e) {
                    System.err.println("Erro ao deletar arquivo: " + e.getMessage());
                }
            }

            usuario.setFotoPerfil(null);
            usuarioDAO.atualizar(usuario);

            return ResponseEntity.ok(new SuccessResponse("Foto removida com sucesso"));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao remover foto: " + e.getMessage()));
        }
    }

    // DTOs
    static class ClienteRequestDTO {
        private String nome;
        private String email;
        private String senha;
        private String confirmarSenha;
        private String telefone;
        private String enderecoPadrao;

        public ClienteRequestDTO() {}

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
        public String getConfirmarSenha() { return confirmarSenha; }
        public void setConfirmarSenha(String confirmarSenha) { this.confirmarSenha = confirmarSenha; }
        public String getTelefone() { return telefone; }
        public void setTelefone(String telefone) { this.telefone = telefone; }
        public String getEnderecoPadrao() { return enderecoPadrao; }
        public void setEnderecoPadrao(String enderecoPadrao) { this.enderecoPadrao = enderecoPadrao; }
    }

    static class ClienteResponseDTO {
        private int clienteId;
        private int usuarioId;
        private String nome;
        private String email;

        public ClienteResponseDTO(int clienteId, int usuarioId, String nome, String email) {
            this.clienteId = clienteId;
            this.usuarioId = usuarioId;
            this.nome = nome;
            this.email = email;
        }

        public int getClienteId() { return clienteId; }
        public int getUsuarioId() { return usuarioId; }
        public String getNome() { return nome; }
        public String getEmail() { return email; }
    }

    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }

    static class AtualizarClienteDTO {
        private int usuarioId;
        private int clienteId;
        private String nome;
        private String email;
        private String telefone;
        private String enderecoPadrao;

        public int getUsuarioId() { return usuarioId; }
        public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
        public int getClienteId() { return clienteId; }
        public void setClienteId(int clienteId) { this.clienteId = clienteId; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getTelefone() { return telefone; }
        public void setTelefone(String telefone) { this.telefone = telefone; }
        public String getEnderecoPadrao() { return enderecoPadrao; }
        public void setEnderecoPadrao(String enderecoPadrao) { this.enderecoPadrao = enderecoPadrao; }
    }

    static class FotoResponse {
        private String nomeArquivo;
        private String url;

        public FotoResponse(String nomeArquivo, String url) {
            this.nomeArquivo = nomeArquivo;
            this.url = url;
        }

        public String getNomeArquivo() { return nomeArquivo; }
        public String getUrl() { return url; }
    }

    static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }
}
