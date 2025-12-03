/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.Controller.Outros;

import com.foodly.Config.Conexao;
import com.foodly.DAO.Outros.SuporteAtendimentoDAO;
import com.foodly.DAO.Outros.SuporteMensagemDAO;
import com.foodly.Models.Outros.SuporteAtendimento;
import com.foodly.Models.Outros.SuporteMensagem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class SuporteController {

    private final SuporteAtendimentoDAO suporteAtendimentoDAO;
    private final SuporteMensagemDAO suporteMensagemDAO;

    public SuporteController() {
        this.suporteAtendimentoDAO = new SuporteAtendimentoDAO();
        this.suporteMensagemDAO = new SuporteMensagemDAO();
    }

    /**
     * Abre um atendimento de suporte com mensagem inicial.
     */
    public SuporteAtendimento abrirAtendimento(int usuarioId,
                                               String assunto,
                                               String mensagemInicial) {

        try {
            // Cria o atendimento
            SuporteAtendimento at = new SuporteAtendimento();
            at.setUsuarioId(usuarioId);
            at.setAssunto(assunto);
            at.setStatus("aberto");
            at.setCriadoEm(LocalDateTime.now());
            at.setEncerradoEm(null);

            int atendimentoId = suporteAtendimentoDAO.salvar(at);
            at.setId(atendimentoId);

            // Registra a primeira mensagem do usuário
            SuporteMensagem msg = new SuporteMensagem();
            msg.setAtendimentoId(atendimentoId);
            msg.setRemetenteTipo("usuario");
            msg.setRemetenteUsuarioId(usuarioId);
            msg.setMensagem(mensagemInicial);
            msg.setEnviadoEm(LocalDateTime.now());

            suporteMensagemDAO.salvar(msg);

            System.out.println("🆘 Atendimento de suporte aberto: #" + atendimentoId);
            return at;

        } catch (SQLException e) {
            System.err.println("Erro ao abrir atendimento de suporte: " + e.getMessage());
            return null;
        }
    }

    /**
     * Envia uma nova mensagem em um atendimento existente.
     */
    public void enviarMensagem(int atendimentoId,
                               String remetenteTipo,       // "usuario" ou "atendente"
                               Integer remetenteUsuarioId,
                               String mensagem) {

        SuporteMensagem msg = new SuporteMensagem();
        msg.setAtendimentoId(atendimentoId);
        msg.setRemetenteTipo(remetenteTipo);
        msg.setRemetenteUsuarioId(remetenteUsuarioId);
        msg.setMensagem(mensagem);
        msg.setEnviadoEm(LocalDateTime.now());

        try {
            suporteMensagemDAO.salvar(msg);
            System.out.println("💬 Mensagem registrada no atendimento #" + atendimentoId);
        } catch (SQLException e) {
            System.err.println("Erro ao enviar mensagem de suporte: " + e.getMessage());
        }
    }

    /**
     * Encerra um atendimento de suporte.
     */
    public void encerrarAtendimento(int atendimentoId) {
        String sql = "UPDATE suporte_atendimentos SET status = ?, encerrado_em = ? WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "encerrado");
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, atendimentoId);

            stmt.executeUpdate();
            System.out.println("✅ Atendimento #" + atendimentoId + " encerrado.");

        } catch (SQLException e) {
            System.err.println("Erro ao encerrar atendimento: " + e.getMessage());
        }
    }
}
