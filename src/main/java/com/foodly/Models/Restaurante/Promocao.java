package com.foodly.Models.Restaurante;

import java.time.LocalDateTime;

public class Promocao {
    private int id;
    private int restauranteId;
    private String titulo;
    private String descricao;
    private String tipoDesconto;
    private double valorDesconto;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private boolean ativo;
    private LocalDateTime criadoEm;

    // Construtores
    public Promocao() {}

    public Promocao(int id, int restauranteId, String titulo, String descricao, String tipoDesconto,
                   double valorDesconto, LocalDateTime dataInicio, LocalDateTime dataFim, 
                   boolean ativo, LocalDateTime criadoEm) {
        this.id = id;
        this.restauranteId = restauranteId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipoDesconto = tipoDesconto;
        this.valorDesconto = valorDesconto;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRestauranteId() { return restauranteId; }
    public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipoDesconto() { return tipoDesconto; }
    public void setTipoDesconto(String tipoDesconto) { this.tipoDesconto = tipoDesconto; }

    public double getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(double valorDesconto) { this.valorDesconto = valorDesconto; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
