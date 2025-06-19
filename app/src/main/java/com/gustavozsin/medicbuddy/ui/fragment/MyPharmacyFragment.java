package com.gustavozsin.medicbuddy.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gustavozsin.medicbuddy.R;
import com.gustavozsin.medicbuddy.dao.MedicBuddyDatabase;
import com.gustavozsin.medicbuddy.databinding.FragmentMyPharmacyBinding;
import com.gustavozsin.medicbuddy.ui.activity.FormAddMedicineToStockActivity;
import com.gustavozsin.medicbuddy.ui.adapter.MedicineListAdapter;
import com.gustavozsin.medicbuddy.ui.viewModel.MedicinesViewModel;
import com.gustavozsin.medicbuddy.model.Medicine;

import java.util.List;

public class MyPharmacyFragment extends Fragment {

    private FragmentMyPharmacyBinding binding;
    private MedicineListAdapter adapter;
    private List<Medicine> medicines;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MedicinesViewModel medicinesViewModel = new ViewModelProvider(requireActivity()).get(MedicinesViewModel.class);

        binding = FragmentMyPharmacyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new MedicineListAdapter(requireContext(), () -> {
            new ViewModelProvider(requireActivity()).get(MedicinesViewModel.class)
                    .loadMedicines(MedicBuddyDatabase.getInstance(requireContext()).medicineDAO());
        });
        binding.fragmentMyPharmacyListMedicines.setAdapter(adapter);

        medicinesViewModel.getMedicines().observe(getViewLifecycleOwner(), medicinesList -> {
            medicines = medicinesList;
            adapter.setMedicines(medicinesList);
        });

        medicinesViewModel.loadMedicines(MedicBuddyDatabase.getInstance(requireContext()).medicineDAO());

        binding.fragmentMyPharmacyButtonAddMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), FormAddMedicineToStockActivity.class));
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        MedicinesViewModel medicinesViewModel = new ViewModelProvider(requireActivity()).get(MedicinesViewModel.class);
        medicinesViewModel.loadMedicines(MedicBuddyDatabase.getInstance(requireContext()).medicineDAO());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}