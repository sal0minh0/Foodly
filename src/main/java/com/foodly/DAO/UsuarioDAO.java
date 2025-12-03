package com.foodly.DAO;

import com.foodly.Models.Usuario;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.foodly.Config.Conexao;

public class UsuarioDAO {

    public int salvar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha_hash, telefone, tipo_usuario, criado_em, foto_perfil) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenhaHash());
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, usuario.getTipoUsuario());
            stmt.setTimestamp(6, Timestamp.valueOf(usuario.getCriadoEm() != null ? usuario.getCriadoEm() : LocalDateTime.now()));
            stmt.setString(7, usuario.getFotoPerfil());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Falha ao obter ID do usuário criado");
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, email, senha_hash, telefone, tipo_usuario, criado_em, foto_perfil " +
                     "FROM usuarios WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    u.setSenhaHash(rs.getString("senha_hash"));
                    u.setTelefone(rs.getString("telefone"));
                    u.setTipoUsuario(rs.getString("tipo_usuario"));
                    u.setFotoPerfil(rs.getString("foto_perfil"));

                    Timestamp criadoEmTs = rs.getTimestamp("criado_em");
                    if (criadoEmTs != null) {
                        u.setCriadoEm(criadoEmTs.toLocalDateTime());
                    }

                    return u;
                }
            }
        }
        return null;
    }

    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenhaHash(rs.getString("senha_hash"));
                usuario.setTelefone(rs.getString("telefone"));
                usuario.setTipoUsuario(rs.getString("tipo_usuario"));
                usuario.setFotoPerfil(rs.getString("foto_perfil"));
                
                Timestamp criadoEmTs = rs.getTimestamp("criado_em");
                if (criadoEmTs != null) {
                    usuario.setCriadoEm(criadoEmTs.toLocalDateTime());
                }
                
                return usuario;
            }
            
            return null;
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, email, senha_hash, telefone, tipo_usuario, criado_em, foto_perfil FROM usuarios";
        List<Usuario> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setEmail(rs.getString("email"));
                u.setSenhaHash(rs.getString("senha_hash"));
                u.setTelefone(rs.getString("telefone"));
                u.setTipoUsuario(rs.getString("tipo_usuario"));
                u.setFotoPerfil(rs.getString("foto_perfil"));

                Timestamp criadoEmTs = rs.getTimestamp("criado_em");
                if (criadoEmTs != null) {
                    u.setCriadoEm(criadoEmTs.toLocalDateTime());
                }

                lista.add(u);
            }
        }
        return lista;
    }

    public void atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios " +
                     "SET nome = ?, email = ?, senha_hash = ?, telefone = ?, tipo_usuario = ?, foto_perfil = ? " +
                     "WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenhaHash());
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, usuario.getTipoUsuario());
            stmt.setString(6, usuario.getFotoPerfil());
            stmt.setInt(7, usuario.getId());

            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void resetarAutoIncrement() throws SQLException {
        String sql = "ALTER TABLE usuarios AUTO_INCREMENT = 1";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}
