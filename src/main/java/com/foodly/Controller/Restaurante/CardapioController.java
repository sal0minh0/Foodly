package com.foodly.Controller.Restaurante;

import com.foodly.DAO.Restaurante.ProdutoDAO;
import com.foodly.DAO.Restaurante.RestauranteDAO;
import com.foodly.Models.Restaurante.Produto;
import com.foodly.Models.Restaurante.Restaurante;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cardapio")
@CrossOrigin(origins = "*")
public class CardapioController {

    private final ProdutoDAO produtoDAO;
    private final RestauranteDAO restauranteDAO;
    private static final String UPLOAD_DIR = "uploads/produtos/";

    public CardapioController() {
        this.produtoDAO = new ProdutoDAO();
        this.restauranteDAO = new RestauranteDAO();
        
        // Se não existir a pasta uploads, criar
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    @PostMapping("/adicionar")
    public ResponseEntity<?> adicionarProduto(@RequestBody ProdutoRequestDTO request) {
        try {
            // Validações
            if (request.getRestauranteId() <= 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("ID do restaurante é obrigatório"));
            }

            if (request.getNome() == null || request.getNome().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Nome do produto é obrigatório"));
            }

            if (request.getPreco() <= 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Preço deve ser maior que zero"));
            }

            // Verificar se restaurante existe
            Restaurante restaurante = restauranteDAO.buscarPorId(request.getRestauranteId());
            if (restaurante == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Restaurante não encontrado"));
            }

            // Criar produto
            Produto produto = new Produto();
            produto.setRestauranteId(request.getRestauranteId());
            produto.setNome(request.getNome());
            produto.setDescricao(request.getDescricao() != null ? request.getDescricao() : "");
            produto.setPreco(request.getPreco());
            produto.setCategoria(request.getCategoria() != null ? request.getCategoria() : "Outros");
            produto.setImagem(request.getImagem());
            produto.setAtivo(request.isAtivo());

            int produtoId = produtoDAO.salvar(produto);
            produto.setId(produtoId);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ProdutoResponseDTO(produto));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao adicionar produto: " + e.getMessage()));
        }
    }

    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<?> listarProdutosPorRestaurante(@PathVariable int restauranteId) {
        try {
            List<Produto> produtos = produtoDAO.listarPorRestaurante(restauranteId);
            return ResponseEntity.ok(produtos);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao listar produtos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarProdutoPorId(@PathVariable int id) {
        try {
            Produto produto = produtoDAO.buscarPorId(id);
            if (produto == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(produto);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao buscar produto: " + e.getMessage()));
        }
    }

    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarProduto(@RequestBody AtualizarProdutoDTO request) {
        try {
            if (request.getId() <= 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("ID do produto é obrigatório"));
            }

            Produto produto = produtoDAO.buscarPorId(request.getId());
            if (produto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Produto não encontrado"));
            }

            // Atualizar dados
            produto.setNome(request.getNome());
            produto.setDescricao(request.getDescricao() != null ? request.getDescricao() : "");
            produto.setPreco(request.getPreco());
            produto.setCategoria(request.getCategoria() != null ? request.getCategoria() : "Outros");
            produto.setImagem(request.getImagem());
            produto.setAtivo(request.isAtivo());

            produtoDAO.atualizar(produto);

            return ResponseEntity.ok(new SuccessResponse("Produto atualizado com sucesso"));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao atualizar produto: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProduto(@PathVariable int id) {
        try {
            // Buscar produto para obter o nome correto da imagem
            Produto produto = produtoDAO.buscarPorId(id);
            if (produto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Produto não encontrado"));
            }

            // Deletar imagem nos arquivos, isso se ela existir
            if (produto.getImagem() != null && !produto.getImagem().isEmpty()) {
                try {
                    Files.deleteIfExists(Paths.get(UPLOAD_DIR + produto.getImagem()));
                    System.out.println("Imagem deletada: " + produto.getImagem());
                } catch (IOException e) {
                    System.err.println("Erro ao deletar imagem: " + e.getMessage());
                }
            }

            // Deletar produto do banco de dados
            produtoDAO.deletar(id);

            return ResponseEntity.ok(new SuccessResponse("Produto removido com sucesso"));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao remover produto: " + e.getMessage()));
        }
    }

    @PostMapping("/upload-imagem/{produtoId}")
    public ResponseEntity<?> uploadImagemProduto(
            @PathVariable int produtoId,
            @RequestParam("imagem") MultipartFile file) {
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Arquivo não enviado"));
            }

            // Validar tipo de arquivo de imagem
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Apenas imagens são permitidas"));
            }

            // Validar tamanho (máximo 5MB para imagens, para não ficar tão pesado)
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
            String nomeArquivo = produtoId + "_" + UUID.randomUUID().toString() + extensao;
            
            // Salvar o arquivo
            Path path = Paths.get(UPLOAD_DIR + nomeArquivo);
            Files.write(path, file.getBytes());

            // Atualizar produto no banco
            Produto produto = produtoDAO.buscarPorId(produtoId);
            if (produto == null) {
                return ResponseEntity.notFound().build();
            }

            // Deletar imagem antiga, isso se existir
            if (produto.getImagem() != null && !produto.getImagem().isEmpty()) {
                try {
                    Files.deleteIfExists(Paths.get(UPLOAD_DIR + produto.getImagem()));
                } catch (IOException e) {
                    System.err.println("Erro ao deletar imagem antiga: " + e.getMessage());
                }
            }

            produto.setImagem(nomeArquivo);
            produtoDAO.atualizar(produto);

            return ResponseEntity.ok(new ImagemResponse(nomeArquivo, "/uploads/produtos/" + nomeArquivo));

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

    @DeleteMapping("/remover-imagem/{produtoId}")
    public ResponseEntity<?> removerImagemProduto(@PathVariable int produtoId) {
        try {
            Produto produto = produtoDAO.buscarPorId(produtoId);
            if (produto == null) {
                return ResponseEntity.notFound().build();
            }

            if (produto.getImagem() != null && !produto.getImagem().isEmpty()) {
                try {
                    Files.deleteIfExists(Paths.get(UPLOAD_DIR + produto.getImagem()));
                } catch (IOException e) {
                    System.err.println("Erro ao deletar arquivo: " + e.getMessage());
                }
            }

            produto.setImagem(null);
            produtoDAO.atualizar(produto);

            return ResponseEntity.ok(new SuccessResponse("Imagem removida com sucesso"));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao remover imagem: " + e.getMessage()));
        }
    }

    // DTOs
    static class ProdutoRequestDTO {
        private int restauranteId;
        private String nome;
        private String descricao;
        private double preco;
        private String categoria;
        private String imagem;
        private boolean ativo;

        public int getRestauranteId() { return restauranteId; }
        public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public double getPreco() { return preco; }
        public void setPreco(double preco) { this.preco = preco; }
        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }
        public boolean isAtivo() { return ativo; }
        public void setAtivo(boolean ativo) { this.ativo = ativo; }
        public String getImagem() { return imagem; }
        public void setImagem(String imagem) { this.imagem = imagem; }
    }

    static class AtualizarProdutoDTO {
        private int id;
        private String nome;
        private String descricao;
        private double preco;
        private String categoria;
        private String imagem;
        private boolean ativo;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public double getPreco() { return preco; }
        public void setPreco(double preco) { this.preco = preco; }
        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }
        public boolean isAtivo() { return ativo; }
        public void setAtivo(boolean ativo) { this.ativo = ativo; }
        public String getImagem() { return imagem; }
        public void setImagem(String imagem) { this.imagem = imagem; }
    }

    static class ProdutoResponseDTO {
        private int id;
        private int restauranteId;
        private String nome;
        private String descricao;
        private double preco;
        private String categoria;
        private String imagem;
        private boolean ativo;

        public ProdutoResponseDTO(Produto produto) {
            this.id = produto.getId();
            this.restauranteId = produto.getRestauranteId();
            this.nome = produto.getNome();
            this.descricao = produto.getDescricao();
            this.preco = produto.getPreco();
            this.categoria = produto.getCategoria();
            this.imagem = produto.getImagem();
            this.ativo = produto.isAtivo();
        }

        public int getId() { return id; }
        public int getRestauranteId() { return restauranteId; }
        public String getNome() { return nome; }
        public String getDescricao() { return descricao; }
        public double getPreco() { return preco; }
        public String getCategoria() { return categoria; }
        public boolean isAtivo() { return ativo; }
        public String getImagem() { return imagem; }
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

    static class ImagemResponse {
        private String nomeArquivo;
        private String url;

        public ImagemResponse(String nomeArquivo, String url) {
            this.nomeArquivo = nomeArquivo;
            this.url = url;
        }

        public String getNomeArquivo() { return nomeArquivo; }
        public String getUrl() { return url; }
    }
}
