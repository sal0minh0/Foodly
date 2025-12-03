package com.foodly.DAO.Cliente;

import com.foodly.Models.Cliente.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.foodly.Config.Conexao;

public class ClienteDAO {

    public int salvar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (usuario_id, endereco_padrao) VALUES (?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, cliente.getUsuarioId());
            stmt.setString(2, cliente.getEnderecoPadrao());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    cliente.setId(idGerado);
                    return idGerado;
                }
            }
        }
        return -1;
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, usuario_id, endereco_padrao FROM clientes WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setId(rs.getInt("id"));
                    c.setUsuarioId(rs.getInt("usuario_id"));
                    c.setEnderecoPadrao(rs.getString("endereco_padrao"));
                    return c;
                }
            }
        }
        return null;
    }

    public Cliente buscarPorUsuarioId(int usuarioId) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE usuario_id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setUsuarioId(rs.getInt("usuario_id"));
                cliente.setEnderecoPadrao(rs.getString("endereco_padrao"));
                return cliente;
            }
            
            return null;
        }
    }

    public List<Cliente> listarTodos() throws SQLException {
        String sql = "SELECT id, usuario_id, endereco_padrao FROM clientes";
        List<Cliente> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setUsuarioId(rs.getInt("usuario_id"));
                c.setEnderecoPadrao(rs.getString("endereco_padrao"));
                lista.add(c);
            }
        }
        return lista;
    }

    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET usuario_id = ?, endereco_padrao = ? WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cliente.getUsuarioId());
            stmt.setString(2, cliente.getEnderecoPadrao());
            stmt.setInt(3, cliente.getId());

            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void resetarAutoIncrement() throws SQLException {
        String sql = "ALTER TABLE clientes AUTO_INCREMENT = 1";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}
