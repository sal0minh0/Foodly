/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.Models.Outros;

import java.time.LocalDateTime;

public class SuporteMensagem {

    private Integer id;
    private Integer atendimentoId;
    private String remetenteTipo;
    private Integer remetenteUsuarioId;
    private String mensagem;
    private LocalDateTime enviadoEm;

    public SuporteMensagem() {
    }

    public SuporteMensagem(Integer id,
                           Integer atendimentoId,
                           String remetenteTipo,
                           Integer remetenteUsuarioId,
                           String mensagem,
                           LocalDateTime enviadoEm) {
        this.id = id;
        this.atendimentoId = atendimentoId;
        this.remetenteTipo = remetenteTipo;
        this.remetenteUsuarioId = remetenteUsuarioId;
        this.mensagem = mensagem;
        this.enviadoEm = enviadoEm;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getAtendimentoId() { return atendimentoId; }
    public void setAtendimentoId(Integer atendimentoId) { this.atendimentoId = atendimentoId; }

    public String getRemetenteTipo() { return remetenteTipo; }
    public void setRemetenteTipo(String remetenteTipo) { this.remetenteTipo = remetenteTipo; }

    public Integer getRemetenteUsuarioId() { return remetenteUsuarioId; }
    public void setRemetenteUsuarioId(Integer remetenteUsuarioId) { this.remetenteUsuarioId = remetenteUsuarioId; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDateTime getEnviadoEm() { return enviadoEm; }
    public void setEnviadoEm(LocalDateTime enviadoEm) { this.enviadoEm = enviadoEm; }
}
