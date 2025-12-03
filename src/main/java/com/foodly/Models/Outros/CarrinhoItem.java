/**
 *  Não usado, mas deixar para implementações futuras
 */
package com.foodly.Models.Outros;

public class CarrinhoItem {

    private Integer id;
    private Integer carrinhoId;
    private Integer produtoId;
    private int quantidade;
    private double precoUnitario;

    public CarrinhoItem() {
    }

    public CarrinhoItem(Integer id, Integer carrinhoId, Integer produtoId, int quantidade, double precoUnitario) {
        this.id = id;
        this.carrinhoId = carrinhoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCarrinhoId() { return carrinhoId; }
    public void setCarrinhoId(Integer carrinhoId) { this.carrinhoId = carrinhoId; }

    public Integer getProdutoId() { return produtoId; }
    public void setProdutoId(Integer produtoId) { this.produtoId = produtoId; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }
}
