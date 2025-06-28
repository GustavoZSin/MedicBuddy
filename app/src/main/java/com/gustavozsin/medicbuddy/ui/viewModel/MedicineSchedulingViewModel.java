package com.gustavozsin.medicbuddy.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gustavozsin.medicbuddy.dao.MedicineSchedulingDAO;
import com.gustavozsin.medicbuddy.model.MedicineScheduling;

import java.util.List;

public class MedicineSchedulingViewModel extends ViewModel {
    private final MutableLiveData<List<MedicineScheduling>> todaySchedulings = new MutableLiveData<>();

    public LiveData<List<MedicineScheduling>> getTodaySchedulings() {
        return todaySchedulings;
    }

    public void loadTodaySchedulings(MedicineSchedulingDAO dao, String today) {
        todaySchedulings.setValue(dao.getTodaySchedulings(today));
    }

    public void loadSchedulings(MedicineSchedulingDAO dao) {
        todaySchedulings.setValue(dao.getAll());
    }
}
