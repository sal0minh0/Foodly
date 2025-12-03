package com.foodly.DAO.Restaurante;

import com.foodly.Models.Restaurante.Restaurante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.foodly.Config.Conexao;

public class RestauranteDAO {

    public int salvar(Restaurante r) throws SQLException {
        String sql = "INSERT INTO restaurantes (usuario_id, nome_fantasia, cnpj, endereco, dados_bancarios, ativo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, r.getUsuarioId());
            stmt.setString(2, r.getNomeFantasia());
            stmt.setString(3, r.getCnpj());
            stmt.setString(4, r.getEndereco());
            stmt.setString(5, r.getDadosBancarios());
            stmt.setBoolean(6, r.isAtivo());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    r.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public Restaurante buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, usuario_id, nome_fantasia, cnpj, endereco, dados_bancarios, ativo " +
                     "FROM restaurantes WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Restaurante r = new Restaurante();
                    r.setId(rs.getInt("id"));
                    r.setUsuarioId(rs.getInt("usuario_id"));
                    r.setNomeFantasia(rs.getString("nome_fantasia"));
                    r.setCnpj(rs.getString("cnpj"));
                    r.setEndereco(rs.getString("endereco"));
                    r.setDadosBancarios(rs.getString("dados_bancarios"));
                    r.setAtivo(rs.getBoolean("ativo"));
                    return r;
                }
            }
        }
        return null;
    }

    public List<Restaurante> listarTodos() throws SQLException {
        String sql = "SELECT id, usuario_id, nome_fantasia, cnpj, endereco, dados_bancarios, ativo FROM restaurantes";
        List<Restaurante> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Restaurante r = new Restaurante();
                r.setId(rs.getInt("id"));
                r.setUsuarioId(rs.getInt("usuario_id"));
                r.setNomeFantasia(rs.getString("nome_fantasia"));
                r.setCnpj(rs.getString("cnpj"));
                r.setEndereco(rs.getString("endereco"));
                r.setDadosBancarios(rs.getString("dados_bancarios"));
                r.setAtivo(rs.getBoolean("ativo"));
                lista.add(r);
            }
        }
        return lista;
    }

    public void atualizar(Restaurante restaurante) throws SQLException {
        String sql = "UPDATE restaurantes SET nome_fantasia = ?, cnpj = ?, endereco = ?, dados_bancarios = ?, ativo = ? WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, restaurante.getNomeFantasia());
            stmt.setString(2, restaurante.getCnpj());
            stmt.setString(3, restaurante.getEndereco());
            stmt.setString(4, restaurante.getDadosBancarios());
            stmt.setBoolean(5, restaurante.isAtivo());
            stmt.setInt(6, restaurante.getId());
            
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM restaurantes WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void resetarAutoIncrement() throws SQLException {
        String sql = "ALTER TABLE restaurantes AUTO_INCREMENT = 1";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.executeUpdate();
        }
    }
}
