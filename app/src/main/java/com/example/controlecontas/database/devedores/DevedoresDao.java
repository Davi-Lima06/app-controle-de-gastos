package com.example.controlecontas.database.devedores;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DevedoresDao {
    @Insert
    long inserirDevedor(Devedor devedor);

    @Query("SELECT * FROM devedores WHERE id = :id")
    Devedor obterPorId(int id);

    @Update
    void atualizarDespesa(Devedor devedorBanco);

    @Query("SELECT * FROM devedores WHERE isPago = 'N' ORDER BY id ASC")
    List<Devedor> obterDevedores();

    @Query("SELECT * FROM devedores WHERE isPago = 'S' ORDER BY id DESC")
    List<Devedor> obterDevedoresPagos();
}
