/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.Models.Outros;

import java.time.LocalDateTime;

public class AvaliacaoRestaurante {

    private Integer id;
    private Integer clienteId;
    private Integer restauranteId;
    private Integer pedidoId;
    private int nota;
    private String comentario;
    private LocalDateTime criadoEm;

    public AvaliacaoRestaurante() {
    }

    public AvaliacaoRestaurante(Integer id,
                                Integer clienteId,
                                Integer restauranteId,
                                Integer pedidoId,
                                int nota,
                                String comentario,
                                LocalDateTime criadoEm) {
        this.id = id;
        this.clienteId = clienteId;
        this.restauranteId = restauranteId;
        this.pedidoId = pedidoId;
        this.nota = nota;
        this.comentario = comentario;
        this.criadoEm = criadoEm;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public Integer getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Integer restauranteId) { this.restauranteId = restauranteId; }

    public Integer getPedidoId() { return pedidoId; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }

    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
