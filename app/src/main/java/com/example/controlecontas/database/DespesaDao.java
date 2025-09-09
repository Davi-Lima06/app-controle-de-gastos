package com.example.controlecontas.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface DespesaDao {

    @Insert
    long inserirDespesa(Despesa despesa);

    @Update
    int atualizarDespesa(Despesa despesa);

    @Delete
    void deletarDespesa(Despesa despesa);

    @Query("SELECT * FROM despesas ORDER BY date(dataDespesa) DESC")
    List<Despesa> listarDespesas();

    @Query("SELECT * FROM despesas WHERE categoria = :categoriaFiltro AND date(dataDespesa) BETWEEN date(:dataInicio) AND date(:dataFim) ORDER BY date(dataDespesa) DESC")
    List<Despesa> obterDespesasPorCategoriaEData(String categoriaFiltro, String dataInicio, String dataFim);

    @Query("SELECT * FROM despesas ORDER BY id DESC LIMIT 15")
    List<Despesa> obterDespesasAdicionadasRecentemente();

    @Query("SELECT * FROM despesas WHERE date(dataDespesa) BETWEEN date(:dataInicio) AND date(:dataFim)")
    List<Despesa> obterDespesasPorData(String dataInicio, String dataFim);

    @Query("SELECT * FROM despesas WHERE id = :id")
    Despesa obterPorId(int id);

    @Query("SELECT * FROM despesas WHERE isCartao = 1 AND date(dataDespesa) BETWEEN date(:dataInicio) AND date(:dataFim)")
    List<Despesa> obterDespesasPorDataECartao(String dataInicio, String dataFim);
    @Query("SELECT * FROM despesas WHERE isCartao = 1 AND categoria = :categoriaAtual AND date(dataDespesa) BETWEEN date(:dataInicio) AND date(:dataFim) ORDER BY date(dataDespesa) DESC")
    List<Despesa> obterDespesasPorCategoriaEDataCartao(String categoriaAtual, String dataInicio, String dataFim);
}
