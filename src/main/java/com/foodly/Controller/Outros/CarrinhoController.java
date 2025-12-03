/**
 *  Não usado, mas usar para implmentações futuras
 */
package com.foodly.Controller.Outros;

import com.foodly.DAO.Outros.CarrinhoDAO;
import com.foodly.DAO.Outros.CarrinhoItemDAO;
import com.foodly.Config.Conexao;
import com.foodly.DAO.Restaurante.ProdutoDAO;
import com.foodly.Models.Outros.Carrinho;
import com.foodly.Models.Outros.CarrinhoItem;
import com.foodly.Models.Restaurante.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class CarrinhoController {

    private final CarrinhoDAO carrinhoDAO;
    private final CarrinhoItemDAO carrinhoItemDAO;
    private final ProdutoDAO produtoDAO;

    public CarrinhoController() {
        this.carrinhoDAO = new CarrinhoDAO();
        this.carrinhoItemDAO = new CarrinhoItemDAO();
        this.produtoDAO = new ProdutoDAO();
    }

    /**
     * Cria um novo carrinho "aberto" para o cliente.
     * Início do fluxo de carrinho.
     */
    public Carrinho criarCarrinho(int clienteId) {
        Carrinho c = new Carrinho();
        c.setClienteId(clienteId);
        c.setStatus("aberto");
        c.setCriadoEm(LocalDateTime.now());
        c.setAtualizadoEm(LocalDateTime.now());

        try {
            int id = carrinhoDAO.salvar(c);
            c.setId(id);
            return c;
        } catch (SQLException e) {
            System.err.println("Erro ao criar carrinho: " + e.getMessage());
            return null;
        }
    }

    /**
     * Adiciona um produto ao carrinho, salvando o preço atual do produto no item.
     */
    public CarrinhoItem adicionarItemAoCarrinho(int carrinhoId, int produtoId, int quantidade) {
        try {
            Produto produto = produtoDAO.buscarPorId(produtoId);
            if (produto == null) {
                System.err.println("Produto não encontrado: id=" + produtoId);
                return null;
            }

            CarrinhoItem item = new CarrinhoItem();
            item.setCarrinhoId(carrinhoId);
            item.setProdutoId(produtoId);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(produto.getPreco());

            int id = carrinhoItemDAO.salvar(item);
            item.setId(id);
            return item;

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar item ao carrinho: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista todos os itens de um carrinho.
     */
    public List<CarrinhoItem> listarItensDoCarrinho(int carrinhoId) {
        try {
            return carrinhoItemDAO.listarPorCarrinho(carrinhoId);
        } catch (SQLException e) {
            System.err.println("Erro ao listar itens do carrinho: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Calcula o valor total do carrinho (soma quantidade * preço_unitário).
     */
    public double calcularTotalCarrinho(int carrinhoId) {
        try {
            List<CarrinhoItem> itens = carrinhoItemDAO.listarPorCarrinho(carrinhoId);
            double total = 0.0;
            for (CarrinhoItem item : itens) {
                total += item.getQuantidade() * item.getPrecoUnitario();
            }
            return total;
        } catch (SQLException e) {
            System.err.println("Erro ao calcular total do carrinho: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Fecha o carrinho para seguir para checkout/pedido.
     * Aqui atualizamos apenas o status e o atualizado_em diretamente via SQL.
     */
    public void fecharCarrinho(int carrinhoId) {
        String sql = "UPDATE carrinhos SET status = ?, atualizado_em = ? WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "fechado");
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, carrinhoId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar carrinho: " + e.getMessage());
        }
    }
}
