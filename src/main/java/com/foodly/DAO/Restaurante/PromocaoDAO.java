/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.DAO.Restaurante;

import com.foodly.Config.Conexao;
import com.foodly.Models.Restaurante.Promocao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class PromocaoDAO {

    public int salvar(Promocao promocao) throws SQLException {
        String sql = "INSERT INTO promocoes (restaurante_id, titulo, descricao, tipo_desconto, valor_desconto, data_inicio, data_fim, ativo, criado_em) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, promocao.getRestauranteId());
            stmt.setString(2, promocao.getTitulo());
            stmt.setString(3, promocao.getDescricao());
            stmt.setString(4, promocao.getTipoDesconto());
            stmt.setDouble(5, promocao.getValorDesconto());
            stmt.setTimestamp(6, Timestamp.valueOf(promocao.getDataInicio()));
            stmt.setTimestamp(7, Timestamp.valueOf(promocao.getDataFim()));
            stmt.setBoolean(8, promocao.isAtivo());
            stmt.setTimestamp(9, Timestamp.valueOf(promocao.getCriadoEm()));
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return -1;
    }

    public Promocao buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM promocoes WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearPromocao(rs);
            }
        }
        
        return null;
    }

    public List<Promocao> listarPorRestaurante(int restauranteId) throws SQLException {
        String sql = "SELECT * FROM promocoes WHERE restaurante_id = ? ORDER BY criado_em DESC";
        List<Promocao> promocoes = new ArrayList<>();
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, restauranteId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Promocao promocao = mapearPromocao(rs);
                
                // Desativar automaticamente se expirou
                if (promocao.getDataFim().isBefore(LocalDateTime.now()) && promocao.isAtivo()) {
                    promocao.setAtivo(false);
                    atualizarStatus(promocao.getId(), false);
                }
                
                promocoes.add(promocao);
            }
        }
        
        return promocoes;
    }

    private void atualizarStatus(int id, boolean ativo) throws SQLException {
        String sql = "UPDATE promocoes SET ativo = ? WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, ativo);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void atualizar(Promocao promocao) throws SQLException {
        String sql = "UPDATE promocoes SET titulo = ?, descricao = ?, tipo_desconto = ?, valor_desconto = ?, data_inicio = ?, data_fim = ?, ativo = ? WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, promocao.getTitulo());
            stmt.setString(2, promocao.getDescricao());
            stmt.setString(3, promocao.getTipoDesconto());
            stmt.setDouble(4, promocao.getValorDesconto());
            stmt.setTimestamp(5, Timestamp.valueOf(promocao.getDataInicio()));
            stmt.setTimestamp(6, Timestamp.valueOf(promocao.getDataFim()));
            stmt.setBoolean(7, promocao.isAtivo());
            stmt.setInt(8, promocao.getId());
            
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM promocoes WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Promocao mapearPromocao(ResultSet rs) throws SQLException {
        Promocao promocao = new Promocao();
        promocao.setId(rs.getInt("id"));
        promocao.setRestauranteId(rs.getInt("restaurante_id"));
        promocao.setTitulo(rs.getString("titulo"));
        promocao.setDescricao(rs.getString("descricao"));
        promocao.setTipoDesconto(rs.getString("tipo_desconto"));
        promocao.setValorDesconto(rs.getDouble("valor_desconto"));
        promocao.setDataInicio(rs.getTimestamp("data_inicio").toLocalDateTime());
        promocao.setDataFim(rs.getTimestamp("data_fim").toLocalDateTime());
        promocao.setAtivo(rs.getBoolean("ativo"));
        promocao.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
        return promocao;
    }
}
