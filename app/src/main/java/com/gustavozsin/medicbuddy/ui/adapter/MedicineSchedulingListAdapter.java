package com.gustavozsin.medicbuddy.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gustavozsin.medicbuddy.R;
import com.gustavozsin.medicbuddy.dao.MedicBuddyDatabase;
import com.gustavozsin.medicbuddy.model.MedicineScheduling;

import java.util.List;

public class MedicineSchedulingListAdapter extends BaseAdapter {
    private List<MedicineScheduling> schedulings;
    private final LayoutInflater inflater;
    private final Context context;
    private final Runnable onDataChanged;

    public MedicineSchedulingListAdapter(Context context, Runnable onDataChanged) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.onDataChanged = onDataChanged;
    }

    public void setSchedulings(List<MedicineScheduling> schedulings) {
        this.schedulings = schedulings;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return schedulings != null ? schedulings.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return schedulings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return schedulings.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_medicine_scheduling, parent, false);
        }
        MedicineScheduling scheduling = schedulings.get(position);

        TextView name = view.findViewById(R.id.item_medicine_scheduling_name);
        ImageButton delete = view.findViewById(R.id.item_medicine_scheduling_delete);

        // Exibe: data - horário - nome - quantidade unidade
        String displayText = scheduling.getStartDate() + " - " +
                scheduling.getFirstDoseHour() + " - " +
                scheduling.getName() + " - " +
                scheduling.getDose() + " " +
                scheduling.getDoseUnit();
        name.setText(displayText);

        delete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.confirm_delete_scheduling)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        MedicBuddyDatabase.getInstance(context).medicineSchedulingDAO().delete(scheduling);
                        if (onDataChanged != null) onDataChanged.run();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        return view;
    }
}