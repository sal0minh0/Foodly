/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.DAO.Outros;

import com.foodly.Models.Outros.Pedido;

import java.sql.*;
import java.time.LocalDateTime;
import com.foodly.Config.Conexao;

public class PedidoDAO {

    public int salvar(Pedido p) throws SQLException {
        String sql = "INSERT INTO pedidos (cliente_id, restaurante_id, carrinho_id, valor_total, status, endereco_entrega, criado_em, atualizado_em) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, p.getClienteId());
            stmt.setInt(2, p.getRestauranteId());
            if (p.getCarrinhoId() != null) {
                stmt.setInt(3, p.getCarrinhoId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setDouble(4, p.getValorTotal());
            stmt.setString(5, p.getStatus());
            stmt.setString(6, p.getEnderecoEntrega());
            stmt.setTimestamp(7, Timestamp.valueOf(
                    p.getCriadoEm() != null ? p.getCriadoEm() : LocalDateTime.now()));
            stmt.setTimestamp(8, Timestamp.valueOf(
                    p.getAtualizadoEm() != null ? p.getAtualizadoEm() : LocalDateTime.now()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    p.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public Pedido buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, cliente_id, restaurante_id, carrinho_id, valor_total, status, endereco_entrega, criado_em, atualizado_em " +
                     "FROM pedidos WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Pedido p = new Pedido();
                    p.setId(rs.getInt("id"));
                    p.setClienteId(rs.getInt("cliente_id"));
                    p.setRestauranteId(rs.getInt("restaurante_id"));

                    int carrinhoId = rs.getInt("carrinho_id");
                    if (!rs.wasNull()) {
                        p.setCarrinhoId(carrinhoId);
                    }

                    p.setValorTotal(rs.getDouble("valor_total"));
                    p.setStatus(rs.getString("status"));
                    p.setEnderecoEntrega(rs.getString("endereco_entrega"));

                    Timestamp t1 = rs.getTimestamp("criado_em");
                    Timestamp t2 = rs.getTimestamp("atualizado_em");
                    if (t1 != null) p.setCriadoEm(t1.toLocalDateTime());
                    if (t2 != null) p.setAtualizadoEm(t2.toLocalDateTime());

                    return p;
                }
            }
        }
        return null;
    }
}
