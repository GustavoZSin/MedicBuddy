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
import com.gustavozsin.medicbuddy.model.Medicine;

import java.util.List;

public class MedicineListAdapter extends BaseAdapter {
    private List<Medicine> medicines;
    private final LayoutInflater inflater;
    private final Context context;
    private final Runnable onDataChanged;

    public MedicineListAdapter(Context context, Runnable onDataChanged) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.onDataChanged = onDataChanged;
    }

    public void setMedicines(List<Medicine> medicines) {
        this.medicines = medicines;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return medicines != null ? medicines.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return medicines.get(position);
    }

    @Override
    public long getItemId(int position) {
        return medicines.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_medicine, parent, false);
        }
        Medicine medicine = medicines.get(position);

        TextView name = view.findViewById(R.id.item_medicine_name);
        ImageButton delete = view.findViewById(R.id.item_medicine_delete);

        name.setText(medicine.getMedicineWithQuantity());

        delete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.confirm_delete_medicine)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        MedicBuddyDatabase.getInstance(context).medicineDAO().delete(medicine);
                        if (onDataChanged != null) onDataChanged.run();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        return view;
    }
}
