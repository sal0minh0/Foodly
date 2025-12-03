package com.foodly.Controller;

import com.foodly.DAO.Cliente.ClienteDAO;
import com.foodly.DAO.UsuarioDAO;
import com.foodly.Models.Cliente.Cliente;
import com.foodly.Models.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioDAO usuarioDAO;
    private final ClienteDAO clienteDAO;

    public AuthController() {
        this.usuarioDAO = new UsuarioDAO();
        this.clienteDAO = new ClienteDAO();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Email é obrigatório"));
            }
            
            if (request.getSenha() == null || request.getSenha().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Senha é obrigatória"));
            }

            Usuario usuario = usuarioDAO.buscarPorEmail(request.getEmail());
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Email ou senha incorretos"));
            }

            // Verificar senha 
            if (!usuario.getSenhaHash().equals(request.getSenha())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Email ou senha incorretos"));
            }

            // Buscar dados do cliente
            Cliente cliente = clienteDAO.buscarPorUsuarioId(usuario.getId());
            
            LoginResponseDTO response = new LoginResponseDTO(
                usuario.getId(),
                cliente != null ? cliente.getId() : 0,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                cliente != null ? cliente.getEnderecoPadrao() : "",
                usuario.getTipoUsuario(),
                usuario.getFotoPerfil()
            );

            return ResponseEntity.ok(response);

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao realizar login: " + e.getMessage()));
        }
    }

    @GetMapping("/perfil/{usuarioId}")
    public ResponseEntity<?> buscarPerfil(@PathVariable int usuarioId) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
            
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }

            Cliente cliente = clienteDAO.buscarPorUsuarioId(usuarioId);
            
            LoginResponseDTO response = new LoginResponseDTO(
                usuario.getId(),
                cliente != null ? cliente.getId() : 0,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                cliente != null ? cliente.getEnderecoPadrao() : "",
                usuario.getTipoUsuario(),
                usuario.getFotoPerfil()
            );

            return ResponseEntity.ok(response);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao buscar perfil: " + e.getMessage()));
        }
    }

    // DTOs
    static class LoginRequestDTO {
        private String email;
        private String senha;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }

    static class LoginResponseDTO {
        private int usuarioId;
        private int clienteId;
        private String nome;
        private String email;
        private String telefone;
        private String enderecoPadrao;
        private String tipoUsuario;
        private String fotoPerfil;

        public LoginResponseDTO(int usuarioId, int clienteId, String nome, String email, 
                               String telefone, String enderecoPadrao, String tipoUsuario, String fotoPerfil) {
            this.usuarioId = usuarioId;
            this.clienteId = clienteId;
            this.nome = nome;
            this.email = email;
            this.telefone = telefone;
            this.enderecoPadrao = enderecoPadrao;
            this.tipoUsuario = tipoUsuario;
            this.fotoPerfil = fotoPerfil;
        }

        public int getUsuarioId() { return usuarioId; }
        public int getClienteId() { return clienteId; }
        public String getNome() { return nome; }
        public String getEmail() { return email; }
        public String getTelefone() { return telefone; }
        public String getEnderecoPadrao() { return enderecoPadrao; }
        public String getTipoUsuario() { return tipoUsuario; }
        public String getFotoPerfil() { return fotoPerfil; }
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
