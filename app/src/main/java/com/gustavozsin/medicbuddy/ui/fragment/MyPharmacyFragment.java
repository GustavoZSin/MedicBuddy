package com.gustavozsin.medicbuddy.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

        adapter = new MedicineListAdapter(requireContext(), () -> new ViewModelProvider(requireActivity()).get(MedicinesViewModel.class)
                .loadMedicines(MedicBuddyDatabase.getInstance(requireContext()).medicineDAO()));
        binding.fragmentMyPharmacyListMedicines.setAdapter(adapter);

        medicinesViewModel.getMedicines().observe(getViewLifecycleOwner(), medicinesList -> {
            medicines = medicinesList;
            adapter.setMedicines(medicinesList);
        });

        medicinesViewModel.loadMedicines(MedicBuddyDatabase.getInstance(requireContext()).medicineDAO());

        binding.fragmentMyPharmacyButtonAddMedicine.setOnClickListener(v -> startActivity(new Intent(requireContext(), FormAddMedicineToStockActivity.class)));

        // Esconde o FAB principal se existir na activity
        View fab = getActivity().findViewById(R.id.app_bar_main_fab_new_medicine);
        if (fab != null) {
            fab.setVisibility(View.GONE);
        }

        ListView listView = binding.fragmentMyPharmacyListMedicines;
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Medicine medicine = medicines.get(position);
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.delete)
                    .setMessage(R.string.confirm_delete_medicine)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        MedicBuddyDatabase db = MedicBuddyDatabase.getInstance(requireContext());
                        db.medicineDAO().deleteAllSchedulingsForMedicine(medicine.getName());
                        db.medicineDAO().delete(medicine);
                        new ViewModelProvider(requireActivity()).get(MedicinesViewModel.class)
                                .loadMedicines(db.medicineDAO());
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
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
        // Garante que o FAB volte a aparecer ao sair do fragmento
        View fab = getActivity().findViewById(R.id.app_bar_main_fab_new_medicine);
        if (fab != null) {
            fab.setVisibility(View.VISIBLE);
        }
        binding = null;
    }
}