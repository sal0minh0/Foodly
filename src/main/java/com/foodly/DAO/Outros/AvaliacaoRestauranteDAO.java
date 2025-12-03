/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.DAO.Outros;

import com.foodly.Models.Outros.AvaliacaoRestaurante;
import com.foodly.Config.Conexao;

import java.sql.*;
import java.time.LocalDateTime;

public class AvaliacaoRestauranteDAO {

    public int salvar(AvaliacaoRestaurante a) throws SQLException {
        String sql = "INSERT INTO avaliacoes_restaurantes (cliente_id, restaurante_id, pedido_id, nota, comentario, criado_em) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, a.getClienteId());
            stmt.setInt(2, a.getRestauranteId());
            stmt.setInt(3, a.getPedidoId());
            stmt.setInt(4, a.getNota());
            stmt.setString(5, a.getComentario());
            stmt.setTimestamp(6, Timestamp.valueOf(
                    a.getCriadoEm() != null ? a.getCriadoEm() : LocalDateTime.now()));

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    a.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }
}
