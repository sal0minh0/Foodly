/**
 *  Não usado, mas usar para implmentações futuras
 */
package com.foodly.Controller.Outros;

import com.foodly.Config.Conexao;
import com.foodly.DAO.Outros.EntregaDAO;
import com.foodly.DAO.Outros.EntregaRespostaDAO;
import com.foodly.DAO.Outros.EntregadorDAO;
import com.foodly.Models.Outros.Entrega;
import com.foodly.Models.Outros.EntregaResposta;
import com.foodly.Models.Outros.Entregador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class EntregaController {

    private final EntregaDAO entregaDAO;
    private final EntregaRespostaDAO entregaRespostaDAO;
    private final EntregadorDAO entregadorDAO;

    public EntregaController() {
        this.entregaDAO = new EntregaDAO();
        this.entregaRespostaDAO = new EntregaRespostaDAO();
        this.entregadorDAO = new EntregadorDAO();
    }

    /**
     * Cria uma entrega com rota sugerida
     */
    public Entrega criarEntregaComRota(int pedidoId,
                                       Double distanciaKm,
                                       Integer tempoEstimadoMin,
                                       String rotaSugerida) {

        Entrega e = new Entrega();
        e.setPedidoId(pedidoId);
        e.setEntregadorId(null);              // Ainda não atribuído
        e.setStatus("disponivel");
        e.setRotaSugerida(rotaSugerida);
        e.setDistanciaKm(distanciaKm);
        e.setTempoEstimadoMin(tempoEstimadoMin);
        e.setCriadoEm(LocalDateTime.now());
        e.setAtualizadoEm(LocalDateTime.now());

        try {
            int id = entregaDAO.salvar(e);
            e.setId(id);

            System.out.println("🚚 Entrega criada com rota sugerida para o pedido #" + pedidoId);
            return e;
        } catch (SQLException ex) {
            System.err.println("Erro ao criar entrega: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Entregador aceita uma entrega disponível.
     */
    public void aceitarEntrega(int entregaId, int entregadorId) {
        try {
            Entregador entregador = entregadorDAO.buscarPorId(entregadorId);
            if (entregador == null) {
                System.err.println("Entregador não encontrado: id=" + entregadorId);
                return;
            }

            // Registra resposta
            EntregaResposta resp = new EntregaResposta();
            resp.setEntregaId(entregaId);
            resp.setEntregadorId(entregadorId);
            resp.setResposta("aceito");
            resp.setCriadoEm(LocalDateTime.now());
            entregaRespostaDAO.salvar(resp);

            // Vincula entregador na entrega e atualiza status
            String sql = "UPDATE entregas SET entregador_id = ?, status = ?, atualizado_em = ? WHERE id = ?";

            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, entregadorId);
                stmt.setString(2, "atribuida");
                stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setInt(4, entregaId);

                stmt.executeUpdate();
            }

            System.out.println("✅ Entregador #" + entregadorId + " aceitou a entrega #" + entregaId);

        } catch (SQLException ex) {
            System.err.println("Erro ao aceitar entrega: " + ex.getMessage());
        }
    }

    /**
     * Entregador recusa uma entrega.
     */
    public void recusarEntrega(int entregaId, int entregadorId) {
        try {
            // Registra resposta
            EntregaResposta resp = new EntregaResposta();
            resp.setEntregaId(entregaId);
            resp.setEntregadorId(entregadorId);
            resp.setResposta("recusado");
            resp.setCriadoEm(LocalDateTime.now());
            entregaRespostaDAO.salvar(resp);

            System.out.println("⚠️ Entregador #" + entregadorId + " recusou a entrega #" + entregaId);

        } catch (SQLException ex) {
            System.err.println("Erro ao recusar entrega: " + ex.getMessage());
        }
    }
}
