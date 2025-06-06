package com.gustavozsin.medicbuddy.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gustavozsin.medicbuddy.databinding.FragmentMyPharmacyBinding;
import com.gustavozsin.medicbuddy.ui.activity.FormAddMedicineToStockActivity;
import com.gustavozsin.medicbuddy.ui.viewModel.MedicinesViewModel;

public class MyPharmacyFragment extends Fragment {

    private FragmentMyPharmacyBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MedicinesViewModel medicinesViewModel = new ViewModelProvider(this).get(MedicinesViewModel.class);

        binding = FragmentMyPharmacyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.fragmentMyPharmacyButtonAddMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), FormAddMedicineToStockActivity.class));
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}