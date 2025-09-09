package com.example.controlecontas.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "despesas")
public class Despesa {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nome;
    private String categoria;
    private Double valor;
    private String dataDespesa;
    private boolean isCartao;
    private int parcelas;

    public Despesa(String nomeItem, String categoria, double valor, String dataDespesa, String emoji, boolean isCartao, int parcelas) {
        this.nome = nomeItem;
        this.categoria = categoria;
        this.valor = valor;
        this.dataDespesa = dataDespesa;
        this.isCartao = isCartao;
        this.emoji = emoji;
        this.parcelas = parcelas;
    }

    public Despesa() {

    }

    public String getDataDespesa() {
        return dataDespesa;
    }

    public void setDataDespesa(String dataDespesa) {
        this.dataDespesa = dataDespesa;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    private String emoji;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }


    public boolean isCartao() {
        return isCartao;
    }

    public void setCartao(boolean cartao) {
        isCartao = cartao;
    }

    public int getParcelas() {
        return parcelas;
    }

    public void setParcelas(int parcelas) {
        this.parcelas = parcelas;
    }

    @Override
    public String toString() {
        return "Despesa{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", categoria='" + categoria + '\'' +
                ", valor=" + valor +
                ", dataDespesa='" + dataDespesa + '\'' +
                ", isCartao=" + isCartao +
                ", parcelas=" + parcelas +
                ", emoji='" + emoji + '\'' +
                '}';
    }
}
