/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.DAO.Outros;

import com.foodly.Models.Outros.Entrega;

import java.sql.*;
import java.time.LocalDateTime;
import com.foodly.Config.Conexao;

public class EntregaDAO {

    public int salvar(Entrega e) throws SQLException {
        String sql = "INSERT INTO entregas (pedido_id, entregador_id, status, rota_sugerida, " +
                     "tempo_estimado_min, distancia_km, criado_em, atualizado_em) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, e.getPedidoId());

            if (e.getEntregadorId() != null) {
                stmt.setInt(2, e.getEntregadorId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, e.getStatus());
            stmt.setString(4, e.getRotaSugerida());

            if (e.getTempoEstimadoMin() != null) {
                stmt.setInt(5, e.getTempoEstimadoMin());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            if (e.getDistanciaKm() != null) {
                stmt.setDouble(6, e.getDistanciaKm());
            } else {
                stmt.setNull(6, Types.DOUBLE);
            }

            stmt.setTimestamp(7, Timestamp.valueOf(
                    e.getCriadoEm() != null ? e.getCriadoEm() : LocalDateTime.now()));
            stmt.setTimestamp(8, Timestamp.valueOf(
                    e.getAtualizadoEm() != null ? e.getAtualizadoEm() : LocalDateTime.now()));

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
}
