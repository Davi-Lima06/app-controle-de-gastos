package com.example.controlecontas.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Despesa.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract DespesaDao despesaDao();

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
