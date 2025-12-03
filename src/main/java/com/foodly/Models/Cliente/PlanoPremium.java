package com.foodly.Models.Cliente;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PlanoPremium {
    private int id;
    private String nome;
    private String descricao;
    private BigDecimal valorMensal;
    private int duracaoDias;
    private boolean ativo;
    private LocalDateTime criadoEm;

    public PlanoPremium() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValorMensal() { return valorMensal; }
    public void setValorMensal(BigDecimal valorMensal) { this.valorMensal = valorMensal; }

    public int getDuracaoDias() { return duracaoDias; }
    public void setDuracaoDias(int duracaoDias) { this.duracaoDias = duracaoDias; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
