package com.gustavozsin.medicbuddy.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gustavozsin.medicbuddy.model.Medicine;

import java.util.List;

@Dao
public interface MedicineDAO {
    @Insert
    void insert(Medicine medicine);

    @Query("SELECT * FROM Medicine")
    List<Medicine> getAll();

    @Query("SELECT name FROM Medicine")
    List<String> getAllMedicineNames();

    @Query("SELECT * FROM Medicine WHERE name = :name LIMIT 1")
    Medicine getByName(String name);

    @Update
    void update(Medicine medicine);

    @Delete
    void delete(Medicine medicine);
}