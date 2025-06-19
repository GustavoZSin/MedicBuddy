package com.gustavozsin.medicbuddy.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gustavozsin.medicbuddy.dao.MedicBuddyDatabase;
import com.gustavozsin.medicbuddy.databinding.FragmentMyPharmacyBinding;
import com.gustavozsin.medicbuddy.ui.activity.FormAddMedicineToStockActivity;
import com.gustavozsin.medicbuddy.ui.viewModel.MedicinesViewModel;

public class MyPharmacyFragment extends Fragment {

    private FragmentMyPharmacyBinding binding;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MedicinesViewModel medicinesViewModel = new ViewModelProvider(requireActivity()).get(MedicinesViewModel.class);

        binding = FragmentMyPharmacyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView listView = binding.fragmentMyPharmacyListMedicines;
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        medicinesViewModel.getMedicineNames().observe(getViewLifecycleOwner(), medicineNames -> {
            adapter.clear();
            if (medicineNames != null) {
                adapter.addAll(medicineNames);
            }
            adapter.notifyDataSetChanged();
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
