/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.DAO.Outros;

import com.foodly.Models.Outros.Carrinho;
import com.foodly.Config.Conexao;

import java.sql.*;
import java.time.LocalDateTime;

public class CarrinhoDAO {

    public int salvar(Carrinho c) throws SQLException {
        String sql = "INSERT INTO carrinhos (cliente_id, status, criado_em, atualizado_em) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, c.getClienteId());
            stmt.setString(2, c.getStatus());
            stmt.setTimestamp(3, Timestamp.valueOf(
                    c.getCriadoEm() != null ? c.getCriadoEm() : LocalDateTime.now()));
            stmt.setTimestamp(4, Timestamp.valueOf(
                    c.getAtualizadoEm() != null ? c.getAtualizadoEm() : LocalDateTime.now()));

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    c.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public Carrinho buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, cliente_id, status, criado_em, atualizado_em FROM carrinhos WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Carrinho c = new Carrinho();
                    c.setId(rs.getInt("id"));
                    c.setClienteId(rs.getInt("cliente_id"));
                    c.setStatus(rs.getString("status"));

                    Timestamp t1 = rs.getTimestamp("criado_em");
                    Timestamp t2 = rs.getTimestamp("atualizado_em");
                    if (t1 != null) c.setCriadoEm(t1.toLocalDateTime());
                    if (t2 != null) c.setAtualizadoEm(t2.toLocalDateTime());

                    return c;
                }
            }
        }
        return null;
    }
}
