/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.Controller.Outros;

import com.foodly.DAO.Outros.AvaliacaoEntregadorDAO;
import com.foodly.DAO.Outros.AvaliacaoRestauranteDAO;
import com.foodly.Models.Outros.AvaliacaoEntregador;
import com.foodly.Models.Outros.AvaliacaoRestaurante;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class AvaliacaoController {

    private final AvaliacaoRestauranteDAO avaliacaoRestauranteDAO;
    private final AvaliacaoEntregadorDAO avaliacaoEntregadorDAO;

    public AvaliacaoController() {
        this.avaliacaoRestauranteDAO = new AvaliacaoRestauranteDAO();
        this.avaliacaoEntregadorDAO = new AvaliacaoEntregadorDAO();
    }

    /**
     * Cliente avalia restaurante.
     */
    public void avaliarRestaurante(int clienteId,
                                   int restauranteId,
                                   int pedidoId,
                                   int nota,
                                   String comentario) {

        AvaliacaoRestaurante a = new AvaliacaoRestaurante();
        a.setClienteId(clienteId);
        a.setRestauranteId(restauranteId);
        a.setPedidoId(pedidoId);
        a.setNota(nota);
        a.setComentario(comentario);
        a.setCriadoEm(LocalDateTime.now());

        try {
            avaliacaoRestauranteDAO.salvar(a);
            System.out.println("⭐ Avaliação de restaurante registrada com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao avaliar restaurante: " + e.getMessage());
        }
    }

    /**
     * Cliente avalia entregador.
     */
    public void avaliarEntregador(int clienteId,
                                  int entregadorId,
                                  int pedidoId,
                                  int nota,
                                  String comentario) {

        AvaliacaoEntregador a = new AvaliacaoEntregador();
        a.setClienteId(clienteId);
        a.setEntregadorId(entregadorId);
        a.setPedidoId(pedidoId);
        a.setNota(nota);
        a.setComentario(comentario);
        a.setCriadoEm(LocalDateTime.now());

        try {
            avaliacaoEntregadorDAO.salvar(a);
            System.out.println("⭐ Avaliação de entregador registrada com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao avaliar entregador: " + e.getMessage());
        }
    }
}
