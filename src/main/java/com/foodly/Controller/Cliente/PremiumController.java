package com.foodly.Controller.Cliente;

import com.foodly.DAO.Cliente.AssinaturaPremiumDAO;
import com.foodly.DAO.Cliente.PlanoPremiumDAO;
import com.foodly.DAO.Cliente.ClienteDAO;
import com.foodly.Models.Cliente.AssinaturaPremium;
import com.foodly.Models.Cliente.PlanoPremium;
import com.foodly.Models.Cliente.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/premium")
@CrossOrigin(origins = "*")
public class PremiumController {

    private final AssinaturaPremiumDAO assinaturaDAO;
    private final PlanoPremiumDAO planoDAO;
    private final ClienteDAO clienteDAO;

    public PremiumController() {
        this.assinaturaDAO = new AssinaturaPremiumDAO();
        this.planoDAO = new PlanoPremiumDAO();
        this.clienteDAO = new ClienteDAO();
    }

    // Endpoint para listar planos
    @GetMapping("/planos")
    public ResponseEntity<?> listarPlanos() {
        try {
            List<PlanoPremium> planos = planoDAO.listarTodos();
            return ResponseEntity.ok(planos);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao listar planos: " + e.getMessage()));
        }
    }

    @PostMapping("/criar")
    public ResponseEntity<?> criarAssinatura(@RequestBody CriarAssinaturaDTO request) {
        try {
            // Validar cliente_id
            if (request.getClienteId() <= 0) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("ID do cliente inválido"));
            }

            // Verificar se o cliente existe
            Cliente cliente = clienteDAO.buscarPorId(request.getClienteId());
            if (cliente == null) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Cliente não encontrado no sistema. ID: " + request.getClienteId()));
            }

            // Verificar se já existe assinatura ativa
            AssinaturaPremium assinaturaExistente = assinaturaDAO.buscarPorClienteId(request.getClienteId());
            if (assinaturaExistente != null && "ativa".equals(assinaturaExistente.getStatus())) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Cliente já possui uma assinatura ativa"));
            }

            AssinaturaPremium assinatura = new AssinaturaPremium();
            assinatura.setClienteId(request.getClienteId());
            assinatura.setPlanoId(request.getPlanoId());
            assinatura.setStatus("ativa");
            assinatura.setDataInicio(LocalDateTime.now());
            assinatura.setDataFim(LocalDateTime.now().plusDays(30));
            assinatura.setRenovacaoAutomatica(true);
            assinatura.setMetodoPagamento(request.getMetodoPagamento());
            assinatura.setReferenciaPagamento("REF-" + System.currentTimeMillis());
            assinatura.setCriadoEm(LocalDateTime.now());

            int id = assinaturaDAO.salvar(assinatura);
            assinatura.setId(id);

            return ResponseEntity.status(HttpStatus.CREATED).body(assinatura);

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao criar assinatura: " + e.getMessage()));
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> buscarAssinaturaPorCliente(@PathVariable int clienteId) {
        try {
            AssinaturaPremium assinatura = assinaturaDAO.buscarPorClienteId(clienteId);
            
            if (assinatura == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(assinatura);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao buscar assinatura: " + e.getMessage()));
        }
    }

    @DeleteMapping("/cancelar/{clienteId}")
    public ResponseEntity<?> cancelarAssinatura(@PathVariable int clienteId) {
        try {
            AssinaturaPremium assinatura = assinaturaDAO.buscarPorClienteId(clienteId);
            
            if (assinatura == null) {
                return ResponseEntity.notFound().build();
            }

            // Atualizar status para 'Cancelada'
            boolean sucesso = assinaturaDAO.cancelarAssinatura(clienteId);
            
            if (sucesso) {
                return ResponseEntity.ok(new SuccessResponse("Assinatura cancelada com sucesso"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro ao cancelar assinatura"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao cancelar assinatura: " + e.getMessage()));
        }
    }

    // DTOs
    static class CriarAssinaturaDTO {
        private int clienteId;
        private int planoId;
        private String metodoPagamento;

        public int getClienteId() { return clienteId; }
        public void setClienteId(int clienteId) { this.clienteId = clienteId; }
        public int getPlanoId() { return planoId; }
        public void setPlanoId(int planoId) { this.planoId = planoId; }
        public String getMetodoPagamento() { return metodoPagamento; }
        public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }
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
