package com.example.controlecontas.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.controlecontas.database.despesa.Despesa;
import com.example.controlecontas.database.despesa.DespesaDao;
import com.example.controlecontas.database.devedores.Devedor;
import com.example.controlecontas.database.devedores.DevedoresDao;

@Database(entities = {Despesa.class, Devedor.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract DespesaDao despesaDao();
    public abstract DevedoresDao devedoresDao();

    public static synchronized AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "economias.db")
                    .fallbackToDestructiveMigration() // apaga e recria se mudar o schema
                    .allowMainThreadQueries() // só para teste; depois usar background thread
                    .build();
        }
        return INSTANCE;
    }
}
