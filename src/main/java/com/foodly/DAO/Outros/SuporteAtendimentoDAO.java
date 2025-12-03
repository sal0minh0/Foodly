/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.DAO.Outros;

import com.foodly.Models.Outros.SuporteAtendimento;

import java.sql.*;
import java.time.LocalDateTime;
import com.foodly.Config.Conexao;

public class SuporteAtendimentoDAO {

    public int salvar(SuporteAtendimento s) throws SQLException {
        String sql = "INSERT INTO suporte_atendimentos (usuario_id, assunto, status, criado_em, encerrado_em) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, s.getUsuarioId());
            stmt.setString(2, s.getAssunto());
            stmt.setString(3, s.getStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(
                    s.getCriadoEm() != null ? s.getCriadoEm() : LocalDateTime.now()));

            if (s.getEncerradoEm() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(s.getEncerradoEm()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    s.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }
}
