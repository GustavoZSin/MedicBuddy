package com.gustavozsin.medicbuddy.ui.adapter;

import android.app.AlertDialog;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gustavozsin.medicbuddy.R;
import com.gustavozsin.medicbuddy.dao.MedicBuddyDatabase;
import com.gustavozsin.medicbuddy.model.Medicine;
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
        ImageButton done = view.findViewById(R.id.item_medicine_scheduling_done);

        // Exibe: data - horário - nome - quantidade unidade
        String displayText = scheduling.getStartDate() + " - " +
                scheduling.getFirstDoseHour() + " - " +
                scheduling.getName() + " - " +
                scheduling.getDose() + " " +
                scheduling.getDoseUnit();
        name.setText(displayText);

        // Risco se concluído
        if (scheduling.isDone()) {
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            done.setImageResource(R.drawable.ic_done_square_checked);
        } else {
            name.setPaintFlags(name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            done.setImageResource(R.drawable.ic_done_square_unchecked);
        }

        done.setOnClickListener(v -> {
            boolean wasDone = scheduling.isDone();
            scheduling.setDone(!wasDone);
            MedicBuddyDatabase db = MedicBuddyDatabase.getInstance(context);
            db.medicineSchedulingDAO().update(scheduling);

            if (!wasDone) {
                // Marcou como concluído: cancelar alarme e subtrair do estoque
                cancelAlarm(context, scheduling);
                subtractMedicineFromStock(db, scheduling);
            } else {
                // Marcou como não concluído: reagendar alarme e devolver ao estoque
                scheduleAlarm(context, scheduling);
                addMedicineToStock(db, scheduling);
            }

            notifyDataSetChanged();
            if (onDataChanged != null) onDataChanged.run();
        });

        delete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.confirm_delete_scheduling)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        MedicBuddyDatabase.getInstance(context).medicineSchedulingDAO().delete(scheduling);
                        cancelAlarm(context, scheduling);
                        if (onDataChanged != null) onDataChanged.run();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        return view;
    }

    private void cancelAlarm(Context context, MedicineScheduling scheduling) {
        Intent intent = new Intent(context, com.gustavozsin.medicbuddy.alarm.SchedulingAlarmReceiver.class);
        intent.putExtra("medicine_name", scheduling.getName());
        intent.putExtra("dose", scheduling.getDose() + " " + scheduling.getDoseUnit());
        intent.putExtra("firstDoseHour", scheduling.getFirstDoseHour());

        int requestCode = getUniqueRequestCode(scheduling);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void scheduleAlarm(Context context, MedicineScheduling scheduling) {
        try {
            String[] dateParts = scheduling.getStartDate().split("/");
            String[] timeParts = scheduling.getFirstDoseHour().split(":");
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(
                    Integer.parseInt(dateParts[2]), // year
                    Integer.parseInt(dateParts[1]) - 1, // month
                    Integer.parseInt(dateParts[0]), // day
                    Integer.parseInt(timeParts[0]), // hour
                    Integer.parseInt(timeParts[1]), // minute
                    0
            );

            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                return;
            }

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, com.gustavozsin.medicbuddy.alarm.SchedulingAlarmReceiver.class);
            intent.putExtra("medicine_name", scheduling.getName());
            intent.putExtra("dose", scheduling.getDose() + " " + scheduling.getDoseUnit());
            intent.putExtra("firstDoseHour", scheduling.getFirstDoseHour());

            int requestCode = getUniqueRequestCode(scheduling);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (alarmManager != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                }
            }
        } catch (Exception ignored) {}
    }

    private int getUniqueRequestCode(MedicineScheduling scheduling) {
        // Gera um código único para o PendingIntent baseado nos dados do agendamento
        String key = scheduling.getName() + "_" + scheduling.getStartDate() + "_" + scheduling.getFirstDoseHour();
        return key.hashCode();
    }

    private void subtractMedicineFromStock(MedicBuddyDatabase db, MedicineScheduling scheduling) {
        Medicine medicine = db.medicineDAO().getByName(scheduling.getName());
        if (medicine != null) {
            try {
                double currentQty = Double.parseDouble(medicine.getQuantity());
                double doseQty = Double.parseDouble(scheduling.getDose());
                double newQty = currentQty - doseQty;
                if (newQty < 0) newQty = 0;
                medicine.setQuantity(String.valueOf(newQty));
                db.medicineDAO().update(medicine);
            } catch (Exception ignored) {}
        }
    }

    private void addMedicineToStock(MedicBuddyDatabase db, MedicineScheduling scheduling) {
        Medicine medicine = db.medicineDAO().getByName(scheduling.getName());
        if (medicine != null) {
            try {
                double currentQty = Double.parseDouble(medicine.getQuantity());
                double doseQty = Double.parseDouble(scheduling.getDose());
                double newQty = currentQty + doseQty;
                medicine.setQuantity(String.valueOf(newQty));
                db.medicineDAO().update(medicine);
            } catch (Exception ignored) {}
        }
    }
}