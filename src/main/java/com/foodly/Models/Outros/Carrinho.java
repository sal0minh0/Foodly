/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.Models.Outros;

import java.time.LocalDateTime;

public class Carrinho {

    private Integer id;
    private Integer clienteId;
    private String status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public Carrinho() {
        this.status = "aberto";
    }

    public Carrinho(Integer id, Integer clienteId, String status, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.clienteId = clienteId;
        this.status = status;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
