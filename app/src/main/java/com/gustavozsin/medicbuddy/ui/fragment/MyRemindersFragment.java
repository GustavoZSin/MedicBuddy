package com.gustavozsin.medicbuddy.ui.fragment;

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
import com.gustavozsin.medicbuddy.dao.MedicineSchedulingDAO;
import com.gustavozsin.medicbuddy.model.MedicineScheduling;
import com.gustavozsin.medicbuddy.ui.adapter.MedicineSchedulingListAdapter;
import com.gustavozsin.medicbuddy.ui.viewModel.MedicineSchedulingViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyRemindersFragment extends Fragment {

    private MedicineSchedulingListAdapter adapter;
    private MedicineSchedulingViewModel schedulingViewModel;
    private List<MedicineScheduling> schedulings;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_reminders, container, false);

        ListView listView = root.findViewById(R.id.fragment_my_reminders_list_medicines);

        schedulingViewModel = new ViewModelProvider(requireActivity()).get(MedicineSchedulingViewModel.class);

        adapter = new MedicineSchedulingListAdapter(requireContext(), () -> updateSchedulings());
        listView.setAdapter(adapter);

        schedulingViewModel.getTodaySchedulings().observe(getViewLifecycleOwner(), schedulingsList -> {
            schedulings = schedulingsList;
            adapter.setSchedulings(schedulingsList);
        });

        return root;
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
