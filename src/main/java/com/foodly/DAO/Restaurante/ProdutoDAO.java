package com.foodly.DAO.Restaurante;

import com.foodly.Models.Restaurante.Produto;
import com.foodly.Config.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public int salvar(Produto produto) throws SQLException {
        String sql = "INSERT INTO produtos (restaurante_id, nome, descricao, preco, categoria, imagem, ativo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, produto.getRestauranteId());
            stmt.setString(2, produto.getNome());
            stmt.setString(3, produto.getDescricao());
            stmt.setDouble(4, produto.getPreco());
            stmt.setString(5, produto.getCategoria());
            stmt.setString(6, produto.getImagem());
            stmt.setBoolean(7, produto.isAtivo());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return -1;
    }

    public Produto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearProduto(rs);
            }
        }
        
        return null;
    }

    public List<Produto> listarPorRestaurante(int restauranteId) throws SQLException {
        String sql = "SELECT * FROM produtos WHERE restaurante_id = ? ORDER BY id DESC";
        List<Produto> produtos = new ArrayList<>();
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, restauranteId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                produtos.add(mapearProduto(rs));
            }
        }
        
        return produtos;
    }

    public List<Produto> listarTodos() throws SQLException {
        String sql = "SELECT * FROM produtos ORDER BY id DESC";
        List<Produto> produtos = new ArrayList<>();
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                produtos.add(mapearProduto(rs));
            }
        }
        
        return produtos;
    }

    public void atualizar(Produto produto) throws SQLException {
        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, categoria = ?, imagem = ?, ativo = ? WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setString(4, produto.getCategoria());
            stmt.setString(5, produto.getImagem());
            stmt.setBoolean(6, produto.isAtivo());
            stmt.setInt(7, produto.getId());
            
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM produtos WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void deletarPorRestaurante(int restauranteId) throws SQLException {
        String sql = "DELETE FROM produtos WHERE restaurante_id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, restauranteId);
            stmt.executeUpdate();
        }
    }

    private Produto mapearProduto(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getInt("id"));
        produto.setRestauranteId(rs.getInt("restaurante_id"));
        produto.setNome(rs.getString("nome"));
        produto.setDescricao(rs.getString("descricao"));
        produto.setPreco(rs.getDouble("preco"));
        produto.setCategoria(rs.getString("categoria"));
        produto.setImagem(rs.getString("imagem"));
        produto.setAtivo(rs.getBoolean("ativo"));
        return produto;
    }
}
