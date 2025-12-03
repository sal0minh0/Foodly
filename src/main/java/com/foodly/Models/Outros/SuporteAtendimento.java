/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.Models.Outros;

import java.time.LocalDateTime;

public class SuporteAtendimento {

    private Integer id;
    private Integer usuarioId;
    private String assunto;
    private String status;
    private LocalDateTime criadoEm;
    private LocalDateTime encerradoEm;

    public SuporteAtendimento() {
        this.status = "aberto";
    }

    public SuporteAtendimento(Integer id,
                              Integer usuarioId,
                              String assunto,
                              String status,
                              LocalDateTime criadoEm,
                              LocalDateTime encerradoEm) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.assunto = assunto;
        this.status = status;
        this.criadoEm = criadoEm;
        this.encerradoEm = encerradoEm;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getEncerradoEm() { return encerradoEm; }
    public void setEncerradoEm(LocalDateTime encerradoEm) { this.encerradoEm = encerradoEm; }
}
