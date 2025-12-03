/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.DAO.Outros;

import com.foodly.Models.Outros.Entregador;

import java.sql.*;
import java.time.LocalDateTime;
import com.foodly.Config.Conexao;

public class EntregadorDAO {

    public int salvar(Entregador e) throws SQLException {
        String sql = "INSERT INTO entregadores (usuario_id, veiculo_tipo, documento, ativo, criado_em) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, e.getUsuarioId());
            stmt.setString(2, e.getVeiculoTipo());
            stmt.setString(3, e.getDocumento());
            stmt.setBoolean(4, e.isAtivo());
            stmt.setTimestamp(5, Timestamp.valueOf(
                    e.getCriadoEm() != null ? e.getCriadoEm() : LocalDateTime.now()));

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    e.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public Entregador buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, usuario_id, veiculo_tipo, documento, ativo, criado_em " +
                     "FROM entregadores WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Entregador e = new Entregador();
                    e.setId(rs.getInt("id"));
                    e.setUsuarioId(rs.getInt("usuario_id"));
                    e.setVeiculoTipo(rs.getString("veiculo_tipo"));
                    e.setDocumento(rs.getString("documento"));
                    e.setAtivo(rs.getBoolean("ativo"));

                    Timestamp t = rs.getTimestamp("criado_em");
                    if (t != null) e.setCriadoEm(t.toLocalDateTime());

                    return e;
                }
            }
        }
        return null;
    }
}
