/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.DAO.Outros;

import com.foodly.Models.Outros.PromocaoCliente;

import java.sql.*;
import com.foodly.Config.Conexao;

public class PromocaoClienteDAO {

    public int salvar(PromocaoCliente pc) throws SQLException {
        String sql = "INSERT INTO promocoes_clientes (promocao_id, cliente_id, resgatada, resgatada_em) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pc.getPromocaoId());
            stmt.setInt(2, pc.getClienteId());
            stmt.setBoolean(3, pc.isResgatada());

            if (pc.getResgatadaEm() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(pc.getResgatadaEm()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    pc.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }
}
