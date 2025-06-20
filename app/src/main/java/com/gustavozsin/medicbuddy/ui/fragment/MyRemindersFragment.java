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

import com.gustavozsin.medicbuddy.dao.MedicBuddyDatabase;
import com.gustavozsin.medicbuddy.dao.MedicineSchedulingDAO;
import com.gustavozsin.medicbuddy.databinding.FragmentMyRemindersBinding;
import com.gustavozsin.medicbuddy.ui.viewModel.MedicineSchedulingViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyRemindersFragment extends Fragment {

    private FragmentMyRemindersBinding binding;
    private MedicineSchedulingViewModel schedulingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyRemindersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        schedulingViewModel = new ViewModelProvider(requireActivity()).get(MedicineSchedulingViewModel.class);

        schedulingViewModel.getTodaySchedulings().observe(getViewLifecycleOwner(), schedulings -> {
            ListView medicinesList = binding.fragmentMyRemindersListMedicines;
            medicinesList.setAdapter(
                    new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            schedulings
                    )
            );
        });

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
        updateSchedulings();
    }

    private void updateSchedulings() {
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        MedicineSchedulingDAO schedulingDAO = MedicBuddyDatabase.getInstance(requireContext()).medicineSchedulingDAO();
        schedulingViewModel.loadTodaySchedulings(schedulingDAO, today);
    }
}