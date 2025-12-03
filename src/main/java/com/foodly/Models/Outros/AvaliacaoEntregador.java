/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.Models.Outros;

import java.time.LocalDateTime;

public class AvaliacaoEntregador {

    private Integer id;
    private Integer clienteId;
    private Integer entregadorId;
    private Integer pedidoId;
    private int nota;
    private String comentario;
    private LocalDateTime criadoEm;

    public AvaliacaoEntregador() {
    }

    public AvaliacaoEntregador(Integer id,
                               Integer clienteId,
                               Integer entregadorId,
                               Integer pedidoId,
                               int nota,
                               String comentario,
                               LocalDateTime criadoEm) {
        this.id = id;
        this.clienteId = clienteId;
        this.entregadorId = entregadorId;
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

    public Integer getEntregadorId() { return entregadorId; }
    public void setEntregadorId(Integer entregadorId) { this.entregadorId = entregadorId; }

    public Integer getPedidoId() { return pedidoId; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }

    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
