package com.gustavozsin.medicbuddy.dao;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.gustavozsin.medicbuddy.model.Medicine;

@Database(entities = {Medicine.class}, version = 1)
public abstract class MedicBuddyDatabase extends RoomDatabase {
    public abstract MedicineDAO medicineStockDAO();

    private static volatile MedicBuddyDatabase INSTANCE;

    public static MedicBuddyDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MedicBuddyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MedicBuddyDatabase.class, "medicbuddy-db")
                            .allowMainThreadQueries() // Para simplificação, use operações assíncronas em produção
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}