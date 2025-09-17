package com.example.controlecontas.database.devedores;

import android.os.Build;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "devedores")
public class Devedor {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nomePessoa;
    private Double valor;
    private String motivo;
    private String isPago;
    private String dataEmprestimo;
    private String dataPagamentoEmprestimo;

    public Devedor(String motivo, Double valor, String nomePessoa) {
        this.motivo = motivo;
        this.valor = valor;
        this.nomePessoa = nomePessoa;
        this.isPago = "N";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.dataEmprestimo = LocalDate.now().toString();
        }
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsPago() {
        return isPago;
    }

    public void setIsPago(String isPago) {
        this.isPago = isPago;
    }
    public String getDataPagamentoEmprestimo() {
        return dataPagamentoEmprestimo;
    }

    public void setDataPagamentoEmprestimo(String dataPagamentoEmprestimo) {
        this.dataPagamentoEmprestimo = dataPagamentoEmprestimo;
    }

    public String getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(String dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }
}
