/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.Models.Outros;

import java.time.LocalDateTime;

public class PromocaoCliente {

    private Integer id;
    private Integer promocaoId;
    private Integer clienteId;
    private boolean resgatada;
    private LocalDateTime resgatadaEm;

    public PromocaoCliente() {
        this.resgatada = false;
    }

    public PromocaoCliente(Integer id,
                           Integer promocaoId,
                           Integer clienteId,
                           boolean resgatada,
                           LocalDateTime resgatadaEm) {
        this.id = id;
        this.promocaoId = promocaoId;
        this.clienteId = clienteId;
        this.resgatada = resgatada;
        this.resgatadaEm = resgatadaEm;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPromocaoId() { return promocaoId; }
    public void setPromocaoId(Integer promocaoId) { this.promocaoId = promocaoId; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public boolean isResgatada() { return resgatada; }
    public void setResgatada(boolean resgatada) { this.resgatada = resgatada; }

    public LocalDateTime getResgatadaEm() { return resgatadaEm; }
    public void setResgatadaEm(LocalDateTime resgatadaEm) { this.resgatadaEm = resgatadaEm; }
}
