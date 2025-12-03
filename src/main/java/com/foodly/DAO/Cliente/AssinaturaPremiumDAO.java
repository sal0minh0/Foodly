package com.foodly.DAO.Cliente;

import com.foodly.Models.Cliente.AssinaturaPremium;
import com.foodly.Config.Conexao;

import java.sql.*;

public class AssinaturaPremiumDAO {

    public int salvar(AssinaturaPremium a) throws SQLException {
        String sql = "INSERT INTO assinaturas_premium (cliente_id, plano_id, status, data_inicio, data_fim, " +
                     "renovacao_automatica, metodo_pagamento, referencia_pagamento, criado_em) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, a.getClienteId());
            stmt.setInt(2, a.getPlanoId());
            stmt.setString(3, a.getStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(a.getDataInicio()));
            stmt.setTimestamp(5, a.getDataFim() != null ? Timestamp.valueOf(a.getDataFim()) : null);
            stmt.setBoolean(6, a.isRenovacaoAutomatica());
            stmt.setString(7, a.getMetodoPagamento());
            stmt.setString(8, a.getReferenciaPagamento());
            stmt.setTimestamp(9, Timestamp.valueOf(a.getCriadoEm()));

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public AssinaturaPremium buscarPorClienteId(int clienteId) throws SQLException {
        String sql = "SELECT * FROM assinaturas_premium WHERE cliente_id = ? AND status = 'ativa' " +
                     "ORDER BY criado_em DESC LIMIT 1";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AssinaturaPremium a = new AssinaturaPremium();
                    a.setId(rs.getInt("id"));
                    a.setClienteId(rs.getInt("cliente_id"));
                    a.setPlanoId(rs.getInt("plano_id"));
                    a.setStatus(rs.getString("status"));
                    
                    Timestamp dataInicio = rs.getTimestamp("data_inicio");
                    if (dataInicio != null) {
                        a.setDataInicio(dataInicio.toLocalDateTime());
                    }
                    
                    Timestamp dataFim = rs.getTimestamp("data_fim");
                    if (dataFim != null) {
                        a.setDataFim(dataFim.toLocalDateTime());
                    }
                    
                    a.setRenovacaoAutomatica(rs.getBoolean("renovacao_automatica"));
                    a.setMetodoPagamento(rs.getString("metodo_pagamento"));
                    a.setReferenciaPagamento(rs.getString("referencia_pagamento"));
                    
                    Timestamp criadoEm = rs.getTimestamp("criado_em");
                    if (criadoEm != null) {
                        a.setCriadoEm(criadoEm.toLocalDateTime());
                    }
                    
                    return a;
                }
            }
        }
        return null;
    }

    public boolean cancelarAssinatura(int clienteId) throws SQLException {
        String sql = "UPDATE assinaturas_premium SET status = 'cancelada' WHERE cliente_id = ? AND status = 'ativa'";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
