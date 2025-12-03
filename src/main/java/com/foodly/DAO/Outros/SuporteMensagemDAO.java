/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.DAO.Outros;

import com.foodly.Models.Outros.SuporteMensagem;

import java.sql.*;
import java.time.LocalDateTime;
import com.foodly.Config.Conexao;


public class SuporteMensagemDAO {

    public int salvar(SuporteMensagem m) throws SQLException {
        String sql = "INSERT INTO suporte_mensagens (atendimento_id, remetente_tipo, remetente_usuario_id, mensagem, enviado_em) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, m.getAtendimentoId());
            stmt.setString(2, m.getRemetenteTipo());

            if (m.getRemetenteUsuarioId() != null) {
                stmt.setInt(3, m.getRemetenteUsuarioId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, m.getMensagem());
            stmt.setTimestamp(5, Timestamp.valueOf(
                    m.getEnviadoEm() != null ? m.getEnviadoEm() : LocalDateTime.now()));

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    m.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }
}
