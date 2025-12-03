/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.DAO.Outros;

import com.foodly.Models.Outros.EntregaResposta;

import java.sql.*;
import java.time.LocalDateTime;
import com.foodly.Config.Conexao;

public class EntregaRespostaDAO {

    public int salvar(EntregaResposta r) throws SQLException {
        String sql = "INSERT INTO entrega_respostas (entrega_id, entregador_id, resposta, criado_em) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, r.getEntregaId());
            stmt.setInt(2, r.getEntregadorId());
            stmt.setString(3, r.getResposta());
            stmt.setTimestamp(4, Timestamp.valueOf(
                    r.getCriadoEm() != null ? r.getCriadoEm() : LocalDateTime.now()));

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
}
