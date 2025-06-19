package com.gustavozsin.medicbuddy.dao;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.gustavozsin.medicbuddy.model.Medicine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Medicine.class}, version = 1, exportSchema = false)
public abstract class MedicBuddyDatabase extends RoomDatabase {
    public abstract MedicineDAO medicineDAO();

    private static volatile MedicBuddyDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MedicBuddyDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (MedicBuddyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =  Room.databaseBuilder(context.getApplicationContext(),
                                    MedicBuddyDatabase.class, "medicbuddy_db")
                            .allowMainThreadQueries() // Permite acesso ao banco na main thread (apenas para desenvolvimento)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}