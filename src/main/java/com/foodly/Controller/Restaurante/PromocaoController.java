package com.foodly.Controller.Restaurante;

import com.foodly.DAO.Restaurante.PromocaoDAO;
import com.foodly.DAO.Restaurante.RestauranteDAO;
import com.foodly.Models.Restaurante.Promocao;
import com.foodly.Models.Restaurante.Restaurante;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/promocoes")
@CrossOrigin(origins = "*")
public class PromocaoController {

    private final PromocaoDAO promocaoDAO;
    private final RestauranteDAO restauranteDAO;

    public PromocaoController() {
        this.promocaoDAO = new PromocaoDAO();
        this.restauranteDAO = new RestauranteDAO();
    }

    @PostMapping("/adicionar")
    public ResponseEntity<?> adicionarPromocao(@RequestBody PromocaoRequestDTO request) {
        try {
            // Validações
            if (request.getRestauranteId() <= 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("ID do restaurante é obrigatório"));
            }

            if (request.getTitulo() == null || request.getTitulo().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Título da promoção é obrigatório"));
            }

            if (request.getValorDesconto() <= 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Valor do desconto deve ser maior que zero"));
            }

            if (request.getDataInicio() == null || request.getDataFim() == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Datas de início e fim são obrigatórias"));
            }

            if (request.getDataInicio().isAfter(request.getDataFim())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Data de início deve ser anterior à data de fim"));
            }

            // Verificar se o restaurante existe
            Restaurante restaurante = restauranteDAO.buscarPorId(request.getRestauranteId());
            if (restaurante == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Restaurante não encontrado"));
            }

            // Criar promoção
            Promocao promocao = new Promocao();
            promocao.setRestauranteId(request.getRestauranteId());
            promocao.setTitulo(request.getTitulo());
            promocao.setDescricao(request.getDescricao() != null ? request.getDescricao() : "");
            promocao.setTipoDesconto(request.getTipoDesconto());
            promocao.setValorDesconto(request.getValorDesconto());
            promocao.setDataInicio(request.getDataInicio());
            promocao.setDataFim(request.getDataFim());
            promocao.setAtivo(request.isAtivo());
            promocao.setCriadoEm(LocalDateTime.now());

            int promocaoId = promocaoDAO.salvar(promocao);
            promocao.setId(promocaoId);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PromocaoResponseDTO(promocao));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao adicionar promoção: " + e.getMessage()));
        }
    }

    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<?> listarPromocoesPorRestaurante(@PathVariable int restauranteId) {
        try {
            List<Promocao> promocoes = promocaoDAO.listarPorRestaurante(restauranteId);
            return ResponseEntity.ok(promocoes);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao listar promoções: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPromocaoPorId(@PathVariable int id) {
        try {
            Promocao promocao = promocaoDAO.buscarPorId(id);
            if (promocao == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(promocao);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao buscar promoção: " + e.getMessage()));
        }
    }

    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarPromocao(@RequestBody AtualizarPromocaoDTO request) {
        try {
            if (request.getId() <= 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("ID da promoção é obrigatório"));
            }

            Promocao promocao = promocaoDAO.buscarPorId(request.getId());
            if (promocao == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Promoção não encontrada"));
            }

            // Atualizar dados
            promocao.setTitulo(request.getTitulo());
            promocao.setDescricao(request.getDescricao() != null ? request.getDescricao() : "");
            promocao.setTipoDesconto(request.getTipoDesconto());
            promocao.setValorDesconto(request.getValorDesconto());
            promocao.setDataInicio(request.getDataInicio());
            promocao.setDataFim(request.getDataFim());
            promocao.setAtivo(request.isAtivo());

            promocaoDAO.atualizar(promocao);

            return ResponseEntity.ok(new SuccessResponse("Promoção atualizada com sucesso"));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao atualizar promoção: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPromocao(@PathVariable int id) {
        try {
            Promocao promocao = promocaoDAO.buscarPorId(id);
            if (promocao == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Promoção não encontrada"));
            }

            promocaoDAO.deletar(id);

            return ResponseEntity.ok(new SuccessResponse("Promoção removida com sucesso"));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao deletar promoção: " + e.getMessage()));
        }
    }

    // DTOs
    public static class PromocaoRequestDTO {
        private int restauranteId;
        private String titulo;
        private String descricao;
        private String tipoDesconto;
        private double valorDesconto;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime dataInicio;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime dataFim;
        
        private boolean ativo;

        // Getters e Setters
        public int getRestauranteId() { return restauranteId; }
        public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public String getTipoDesconto() { return tipoDesconto; }
        public void setTipoDesconto(String tipoDesconto) { this.tipoDesconto = tipoDesconto; }
        public double getValorDesconto() { return valorDesconto; }
        public void setValorDesconto(double valorDesconto) { this.valorDesconto = valorDesconto; }
        public LocalDateTime getDataInicio() { return dataInicio; }
        public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
        public LocalDateTime getDataFim() { return dataFim; }
        public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
        public boolean isAtivo() { return ativo; }
        public void setAtivo(boolean ativo) { this.ativo = ativo; }
    }

    public static class AtualizarPromocaoDTO {
        private int id;
        private String titulo;
        private String descricao;
        private String tipoDesconto;
        private double valorDesconto;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime dataInicio;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime dataFim;
        
        private boolean ativo;

        // Getters e Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public String getTipoDesconto() { return tipoDesconto; }
        public void setTipoDesconto(String tipoDesconto) { this.tipoDesconto = tipoDesconto; }
        public double getValorDesconto() { return valorDesconto; }
        public void setValorDesconto(double valorDesconto) { this.valorDesconto = valorDesconto; }
        public LocalDateTime getDataInicio() { return dataInicio; }
        public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
        public LocalDateTime getDataFim() { return dataFim; }
        public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
        public boolean isAtivo() { return ativo; }
        public void setAtivo(boolean ativo) { this.ativo = ativo; }
    }

    public static class PromocaoResponseDTO {
        private int id;
        private int restauranteId;
        private String titulo;
        private String descricao;
        private String tipoDesconto;
        private double valorDesconto;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime dataInicio;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime dataFim;
        
        private boolean ativo;

        public PromocaoResponseDTO(Promocao promocao) {
            this.id = promocao.getId();
            this.restauranteId = promocao.getRestauranteId();
            this.titulo = promocao.getTitulo();
            this.descricao = promocao.getDescricao();
            this.tipoDesconto = promocao.getTipoDesconto();
            this.valorDesconto = promocao.getValorDesconto();
            this.dataInicio = promocao.getDataInicio();
            this.dataFim = promocao.getDataFim();
            this.ativo = promocao.isAtivo();
        }

        // Getters
        public int getId() { return id; }
        public int getRestauranteId() { return restauranteId; }
        public String getTitulo() { return titulo; }
        public String getDescricao() { return descricao; }
        public String getTipoDesconto() { return tipoDesconto; }
        public double getValorDesconto() { return valorDesconto; }
        public LocalDateTime getDataInicio() { return dataInicio; }
        public LocalDateTime getDataFim() { return dataFim; }
        public boolean isAtivo() { return ativo; }
    }

    //DTOS
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }

    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }
}
