/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.DAO.Outros;

import com.foodly.Models.Outros.CarrinhoItem;
import com.foodly.Config.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarrinhoItemDAO {

    /**
     * Salva um novo item no carrinho e retorna o ID gerado.
     */
    public int salvar(CarrinhoItem item) throws SQLException {
        String sql = "INSERT INTO carrinho_itens (carrinho_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, item.getCarrinhoId());
            stmt.setInt(2, item.getProdutoId());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getPrecoUnitario());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Lista todos os itens de um carrinho específico.
     */
    public List<CarrinhoItem> listarPorCarrinho(int carrinhoId) throws SQLException {
        String sql = "SELECT * FROM carrinho_itens WHERE carrinho_id = ?";
        List<CarrinhoItem> itens = new ArrayList<>();
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, carrinhoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CarrinhoItem item = new CarrinhoItem();
                    item.setId(rs.getInt("id"));
                    item.setCarrinhoId(rs.getInt("carrinho_id"));
                    item.setProdutoId(rs.getInt("produto_id"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setPrecoUnitario(rs.getDouble("preco_unitario"));
                    itens.add(item);
                }
            }
        }
        return itens;
    }

    /**
     * Busca um item específico por ID.
     */
    public CarrinhoItem buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM carrinho_itens WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CarrinhoItem item = new CarrinhoItem();
                    item.setId(rs.getInt("id"));
                    item.setCarrinhoId(rs.getInt("carrinho_id"));
                    item.setProdutoId(rs.getInt("produto_id"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setPrecoUnitario(rs.getDouble("preco_unitario"));
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Atualiza a quantidade de um item do carrinho.
     */
    public void atualizar(CarrinhoItem item) throws SQLException {
        String sql = "UPDATE carrinho_itens SET quantidade = ?, preco_unitario = ? WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, item.getQuantidade());
            stmt.setDouble(2, item.getPrecoUnitario());
            stmt.setInt(3, item.getId());
            
            stmt.executeUpdate();
        }
    }

    /**
     * Remove um item do carrinho.
     */
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM carrinho_itens WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Remove todos os itens de um carrinho.
     */
    public void deletarPorCarrinho(int carrinhoId) throws SQLException {
        String sql = "DELETE FROM carrinho_itens WHERE carrinho_id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, carrinhoId);
            stmt.executeUpdate();
        }
    }
}
