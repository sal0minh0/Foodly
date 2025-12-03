/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.DAO.Outros;

import com.foodly.Models.Outros.PedidoItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.foodly.Config.Conexao;

public class PedidoItemDAO {

    public int salvar(PedidoItem item) throws SQLException {
        String sql = "INSERT INTO pedido_itens (pedido_id, produto_id, quantidade, preco_unitario) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, item.getPedidoId());
            stmt.setInt(2, item.getProdutoId());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getPrecoUnitario());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    item.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public List<PedidoItem> listarPorPedido(int pedidoId) throws SQLException {
        String sql = "SELECT id, pedido_id, produto_id, quantidade, preco_unitario " +
                     "FROM pedido_itens WHERE pedido_id = ?";
        List<PedidoItem> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoItem item = new PedidoItem();
                    item.setId(rs.getInt("id"));
                    item.setPedidoId(rs.getInt("pedido_id"));
                    item.setProdutoId(rs.getInt("produto_id"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setPrecoUnitario(rs.getDouble("preco_unitario"));
                    lista.add(item);
                }
            }
        }
        return lista;
    }
}
