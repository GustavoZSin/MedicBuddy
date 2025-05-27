package com.gustavozsin.medicbuddy.dao;

import com.gustavozsin.medicbuddy.model.MedicineScheduling;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicineSchedulingDAO {
    //TODO: alterar para banco de dados depois
    private final static List<MedicineScheduling> schedulings = new ArrayList<>();
    public void save(MedicineScheduling medicineScheduling) {
        schedulings.add(medicineScheduling);
    }

    public List<MedicineScheduling> allSchedulings() {
        return new ArrayList<>(schedulings);
    }
    public List<MedicineScheduling> todaySchedulings() {
        List<MedicineScheduling> result = new ArrayList<>();
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        for (MedicineScheduling scheduling : schedulings) {
            if (today.equals(scheduling.getStartDate())) {
                result.add(scheduling);
            }
        }

        return result;
    }
}