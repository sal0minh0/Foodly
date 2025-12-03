package com.foodly.Models.Restaurante;

public class Restaurante {

    private Integer id;
    private Integer usuarioId;
    private String nomeFantasia;
    private String cnpj;
    private String endereco;
    private String dadosBancarios;
    private boolean ativo;

    public Restaurante() {
        this.ativo = true;
    }

    public Restaurante(Integer id,
                       Integer usuarioId,
                       String nomeFantasia,
                       String cnpj,
                       String endereco,
                       String dadosBancarios,
                       boolean ativo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.dadosBancarios = dadosBancarios;
        this.ativo = ativo;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getDadosBancarios() {
        return dadosBancarios;
    }

    public void setDadosBancarios(String dadosBancarios) {
        this.dadosBancarios = dadosBancarios;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
