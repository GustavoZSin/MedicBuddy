package com.gustavozsin.medicbuddy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gustavozsin.medicbuddy.dao.MedicineSchedulingDAO;
import com.gustavozsin.medicbuddy.databinding.FragmentMyRemindersBinding;
import com.gustavozsin.medicbuddy.ui.viewModel.HomeViewModel;

public class MyRemindersFragment extends Fragment {

    private FragmentMyRemindersBinding binding;
    private final MedicineSchedulingDAO medicineSchedulingDAO = new MedicineSchedulingDAO();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyRemindersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //TODO: Verificar isso depois
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        configureList();
    }

    private void configureList() {
        //TODO: lista somente para exemplo

        ListView medicinesList = binding.fragmentMyRemindersListMedicines;
        medicinesList.setAdapter(
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        medicineSchedulingDAO.todaySchedulings()
                )
        );
    }
}