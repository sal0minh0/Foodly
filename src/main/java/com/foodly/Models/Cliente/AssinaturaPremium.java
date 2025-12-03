package com.foodly.Models.Cliente;

import java.time.LocalDateTime;

public class AssinaturaPremium {
    private int id;
    private int clienteId;
    private int planoId;
    private String status;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private boolean renovacaoAutomatica;
    private String metodoPagamento;
    private String referenciaPagamento;
    private LocalDateTime criadoEm;

    public AssinaturaPremium() {}

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    
    public int getPlanoId() { return planoId; }
    public void setPlanoId(int planoId) { this.planoId = planoId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    
    public boolean isRenovacaoAutomatica() { return renovacaoAutomatica; }
    public void setRenovacaoAutomatica(boolean renovacaoAutomatica) { 
        this.renovacaoAutomatica = renovacaoAutomatica; 
    }
    
    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { 
        this.metodoPagamento = metodoPagamento; 
    }
    
    public String getReferenciaPagamento() { return referenciaPagamento; }
    public void setReferenciaPagamento(String referenciaPagamento) { 
        this.referenciaPagamento = referenciaPagamento; 
    }
    
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
