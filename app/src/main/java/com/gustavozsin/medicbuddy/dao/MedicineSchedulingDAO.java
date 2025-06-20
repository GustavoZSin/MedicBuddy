package com.gustavozsin.medicbuddy.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.gustavozsin.medicbuddy.model.MedicineScheduling;

import java.util.List;

@Dao
public interface MedicineSchedulingDAO {
    @Insert
    void insert(MedicineScheduling medicineScheduling);

    @Query("SELECT * FROM MedicineScheduling")
    List<MedicineScheduling> allSchedulings();

    @Query("SELECT * FROM MedicineScheduling WHERE startDate = :today")
    List<MedicineScheduling> todaySchedulings(String today);
}
