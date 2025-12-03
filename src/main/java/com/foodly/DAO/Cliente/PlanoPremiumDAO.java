package com.foodly.DAO.Cliente;

import com.foodly.Models.Cliente.PlanoPremium;
import com.foodly.Config.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanoPremiumDAO {

    public List<PlanoPremium> listarTodos() throws SQLException {
        String sql = "SELECT * FROM planos_premium WHERE ativo = TRUE ORDER BY id";
        List<PlanoPremium> planos = new ArrayList<>();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PlanoPremium plano = new PlanoPremium();
                plano.setId(rs.getInt("id"));
                plano.setNome(rs.getString("nome"));
                plano.setDescricao(rs.getString("descricao"));
                plano.setValorMensal(rs.getBigDecimal("valor_mensal"));
                plano.setDuracaoDias(rs.getInt("duracao_dias"));
                plano.setAtivo(rs.getBoolean("ativo"));
                
                Timestamp criadoEm = rs.getTimestamp("criado_em");
                if (criadoEm != null) {
                    plano.setCriadoEm(criadoEm.toLocalDateTime());
                }
                
                planos.add(plano);
            }
        }
        return planos;
    }

    public PlanoPremium buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM planos_premium WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PlanoPremium plano = new PlanoPremium();
                    plano.setId(rs.getInt("id"));
                    plano.setNome(rs.getString("nome"));
                    plano.setDescricao(rs.getString("descricao"));
                    plano.setValorMensal(rs.getBigDecimal("valor_mensal"));
                    plano.setDuracaoDias(rs.getInt("duracao_dias"));
                    plano.setAtivo(rs.getBoolean("ativo"));
                    
                    Timestamp criadoEm = rs.getTimestamp("criado_em");
                    if (criadoEm != null) {
                        plano.setCriadoEm(criadoEm.toLocalDateTime());
                    }
                    
                    return plano;
                }
            }
        }
        return null;
    }
}
