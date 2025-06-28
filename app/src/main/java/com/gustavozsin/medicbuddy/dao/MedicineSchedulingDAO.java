package com.gustavozsin.medicbuddy.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.gustavozsin.medicbuddy.model.MedicineScheduling;

import java.util.List;

@Dao
public interface MedicineSchedulingDAO {
    @Insert
    void insert(MedicineScheduling scheduling);

    @Query("SELECT * FROM MedicineScheduling")
    List<MedicineScheduling> getAll();

    @Query("SELECT * FROM MedicineScheduling WHERE startDate = :date")
    List<MedicineScheduling> getTodaySchedulings(String date);

    @Delete
    void delete(MedicineScheduling scheduling);
}
