package com.gustavozsin.medicbuddy.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.gustavozsin.medicbuddy.model.Medicine;

import java.util.List;

@Dao
public interface MedicineDAO {
    @Insert
    void insert(Medicine medicine);

    @Query("SELECT * FROM Medicine")
    List<Medicine> getAll();

    @Delete
    void delete(Medicine medicine);
}