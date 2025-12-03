/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.Models.Outros;

import java.time.LocalDateTime;

public class Entrega {

    private Integer id;
    private Integer pedidoId;
    private Integer entregadorId;
    private String status;
    private String rotaSugerida;
    private Integer tempoEstimadoMin;
    private Double distanciaKm;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public Entrega() {
    }

    public Entrega(Integer id,
                   Integer pedidoId,
                   Integer entregadorId,
                   String status,
                   String rotaSugerida,
                   Integer tempoEstimadoMin,
                   Double distanciaKm,
                   LocalDateTime criadoEm,
                   LocalDateTime atualizadoEm) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.entregadorId = entregadorId;
        this.status = status;
        this.rotaSugerida = rotaSugerida;
        this.tempoEstimadoMin = tempoEstimadoMin;
        this.distanciaKm = distanciaKm;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPedidoId() { return pedidoId; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }

    public Integer getEntregadorId() { return entregadorId; }
    public void setEntregadorId(Integer entregadorId) { this.entregadorId = entregadorId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRotaSugerida() { return rotaSugerida; }
    public void setRotaSugerida(String rotaSugerida) { this.rotaSugerida = rotaSugerida; }

    public Integer getTempoEstimadoMin() { return tempoEstimadoMin; }
    public void setTempoEstimadoMin(Integer tempoEstimadoMin) { this.tempoEstimadoMin = tempoEstimadoMin; }

    public Double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(Double distanciaKm) { this.distanciaKm = distanciaKm; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
