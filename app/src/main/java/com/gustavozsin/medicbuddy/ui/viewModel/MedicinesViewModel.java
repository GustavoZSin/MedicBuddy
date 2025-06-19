package com.gustavozsin.medicbuddy.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gustavozsin.medicbuddy.dao.MedicineDAO;
import com.gustavozsin.medicbuddy.model.Medicine;

import java.util.ArrayList;
import java.util.List;

public class MedicinesViewModel extends ViewModel {
    private final MutableLiveData<List<String>> medicineNames = new MutableLiveData<>();
    private final MutableLiveData<List<Medicine>> medicines = new MutableLiveData<>();

    public LiveData<List<String>> getMedicineNames() {
        return medicineNames;
    }

    public LiveData<List<Medicine>> getMedicines() {
        return medicines;
    }

    public void loadMedicines(MedicineDAO dao) {
        List<Medicine> medicinesList = dao.getAll();
        medicines.setValue(medicinesList);

        List<String> formatedData = formateMedicineNames(medicinesList);

        medicineNames.setValue(formatedData);
    }

    private List<String> formateMedicineNames(List<Medicine> medicines) {
        List<String> formatedData = new ArrayList<>();

        for (Medicine medicine : medicines) {
            formatedData.add(medicine.getMedicineWithQuantity());
        }

        return formatedData;
    }
}