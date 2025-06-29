package com.gustavozsin.medicbuddy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyRemindersFragment extends Fragment {

    private MedicineSchedulingListAdapter adapter;
    private MedicineSchedulingViewModel schedulingViewModel;
    private List<MedicineScheduling> schedulings;
    private Button showFutureButton;
    private Button showTodayButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_reminders, container, false);

        ListView listView = root.findViewById(R.id.fragment_my_reminders_list_medicines);
        showFutureButton = root.findViewById(R.id.fragment_my_reminders_btn_show_future);
        showTodayButton = root.findViewById(R.id.fragment_my_reminders_btn_show_today);

        schedulingViewModel = new ViewModelProvider(requireActivity()).get(MedicineSchedulingViewModel.class);

        adapter = new MedicineSchedulingListAdapter(requireContext(), () -> updateSchedulings());
        listView.setAdapter(adapter);

        schedulingViewModel.getTodaySchedulings().observe(getViewLifecycleOwner(), schedulingsList -> {
            schedulings = schedulingsList;
            adapter.setSchedulings(schedulingsList);
        });

        showFutureButton.setOnClickListener(v -> showFutureSchedulings());
        showTodayButton.setOnClickListener(v -> showTodaySchedulings());

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

    private void showTodaySchedulings() {
        if (schedulings == null) return;
        adapter.setSchedulings(schedulings);
    }

    private void showFutureSchedulings() {
        // Busca todos os agendamentos do banco
        MedicineSchedulingDAO schedulingDAO = MedicBuddyDatabase.getInstance(requireContext()).medicineSchedulingDAO();
        List<MedicineScheduling> allSchedulings = schedulingDAO.getAll();

        List<MedicineScheduling> futureSchedulings = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (MedicineScheduling scheduling : allSchedulings) {
            try {
                String[] dateParts = scheduling.getStartDate().split("/");
                String[] timeParts = scheduling.getFirstDoseHour().split(":");
                Calendar cal = Calendar.getInstance();
                cal.set(
                    Integer.parseInt(dateParts[2]),
                    Integer.parseInt(dateParts[1]) - 1,
                    Integer.parseInt(dateParts[0]),
                    Integer.parseInt(timeParts[0]),
                    Integer.parseInt(timeParts[1]),
                    0
                );
                if (cal.getTimeInMillis() > now) {
                    futureSchedulings.add(scheduling);
                }
            } catch (Exception ignored) {}
        }
        adapter.setSchedulings(futureSchedulings);
    }
}