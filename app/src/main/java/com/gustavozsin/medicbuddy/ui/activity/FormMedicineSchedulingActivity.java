package com.gustavozsin.medicbuddy.ui.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gustavozsin.medicbuddy.R;
import com.gustavozsin.medicbuddy.dao.MedicBuddyDatabase;
import com.gustavozsin.medicbuddy.model.MedicineScheduling;
import com.gustavozsin.medicbuddy.ui.viewModel.MedicineSchedulingViewModel;

import java.util.Calendar;
import java.util.List;

public class FormMedicineSchedulingActivity extends AppCompatActivity {
    private Spinner nameField;
    private EditText medicineDoseField;
    private Spinner doseUnitField;
    private EditText startDateField;
    private Spinner frequencyField;
    private EditText firstDoseHourField;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_scheduling);

        String newMedicineSchedulingTitle = getString(R.string.new_medicine_scheduling);
        setTitle(newMedicineSchedulingTitle);

        initializeFields();
        setupSpinners();
        setupPickers();
        setupSaveButton();
    }

    // <editor-fold desc="Setups">
    private void initializeFields() {
        nameField = findViewById(R.id.activity_medicine_scheduling_medicine_name);
        medicineDoseField = findViewById(R.id.activity_medicine_scheduling_medicine_dose);
        doseUnitField = findViewById(R.id.activity_medicine_scheduling_medicine_dose_unit);
        startDateField = findViewById(R.id.activity_medicine_scheduling_medicine_start_date);
        frequencyField = findViewById(R.id.activity_medicine_scheduling_medicine_frequency);
        firstDoseHourField = findViewById(R.id.activity_medicine_scheduling_medicine_first_dose_hour);
        saveButton = findViewById(R.id.button_save);
    }

    private void setupSpinners() {
        setupNameSpinner();
        setupDoseUnitSpinner();
        setupFrequencySpinner();
    }

    private void setupPickers() {
        setupDatePicker();
        setupTimePicker();
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            MedicineScheduling scheduling = createMedicineSchedulingFromFields();
            if (validateFields(scheduling)) {
                saveScheduling(scheduling);
                scheduleAlarm(scheduling);
            }
        });
    }

    private void setupNameSpinner() {
        MedicBuddyDatabase db = MedicBuddyDatabase.getInstance(this);
        List<String> medicineNames = db.medicineDAO().getAllMedicineNames();
        if (medicineNames == null || medicineNames.isEmpty()) {
            medicineNames = new java.util.ArrayList<>();
            medicineNames.add(getString(R.string.no_medicines_found));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, medicineNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameField.setAdapter(adapter);
    }

    private void setupDoseUnitSpinner() {
        String[] doseUnits = getResources().getStringArray(R.array.dose_units);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doseUnits);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doseUnitField.setAdapter(adapter);
    }

    private void setupFrequencySpinner() {
        String[] frequencies = getResources().getStringArray(R.array.frequencies);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, frequencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencyField.setAdapter(adapter);
    }

    private void setupDatePicker() {
        startDateField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        startDateField.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void setupTimePicker() {
        firstDoseHourField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(
                    this,
                    (TimePicker view, int hourOfDay, int minute) -> {
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        firstDoseHourField.setText(selectedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            ).show();
        });
    }
    // </editor-fold>

    // <editor-fold desc="Criação, Validação, Agendamentos, Banco de Dados">
    @NonNull
    private MedicineScheduling createMedicineSchedulingFromFields() {
        String name = nameField.getSelectedItem().toString();
        String dose = medicineDoseField.getText().toString();
        String doseUnit = doseUnitField.getSelectedItem().toString();
        String startDate = startDateField.getText().toString();
        String frequency = frequencyField.getSelectedItem().toString();
        String firstDoseHour = firstDoseHourField.getText().toString();

        return new MedicineScheduling(name, dose, doseUnit, startDate, frequency, firstDoseHour);
    }

    private boolean validateFields(MedicineScheduling scheduling) {
        if (scheduling.getName().trim().isEmpty() ||
                scheduling.getDose().trim().isEmpty() ||
                scheduling.getStartDate().trim().isEmpty() ||
                scheduling.getFirstDoseHour().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_in_all_required_fields), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveScheduling(MedicineScheduling scheduling) {
        try {
            MedicBuddyDatabase db = MedicBuddyDatabase.getInstance(this);
            db.medicineSchedulingDAO().insert(scheduling);

            MedicineSchedulingViewModel viewModel = new ViewModelProvider(this).get(MedicineSchedulingViewModel.class);
            viewModel.loadSchedulings(db.medicineSchedulingDAO());

            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_save_scheduling_try_again), Toast.LENGTH_LONG).show();
        }
    }

    private void scheduleAlarm(MedicineScheduling scheduling) {
        try {
            String[] dateParts = scheduling.getStartDate().split("/");
            String[] timeParts = scheduling.getFirstDoseHour().split(":");
            Calendar calendar = Calendar.getInstance();
            calendar.set(
                    Integer.parseInt(dateParts[2]), // year
                    Integer.parseInt(dateParts[1]) - 1, // month
                    Integer.parseInt(dateParts[0]), // day
                    Integer.parseInt(timeParts[0]), // hour
                    Integer.parseInt(timeParts[1]), // minute
                    0
            );

            if (!isSchedulingInThePast(calendar) && isPermissionConfigured()) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(this, com.gustavozsin.medicbuddy.alarm.SchedulingAlarmReceiver.class);
                intent.putExtra("medicine_name", scheduling.getName());
                intent.putExtra("dose", scheduling.getDose() + " " + scheduling.getDoseUnit());
                intent.putExtra("firstDoseHour", scheduling.getFirstDoseHour());

                int requestCode = (int) System.currentTimeMillis();
                android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                        this, requestCode, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
                );

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean isPermissionConfigured() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, getString(R.string.error_alarm_exact_permission), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return false;
            }
        }
        return true;
    }
    private boolean isSchedulingInThePast(Calendar calendar) {
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            Toast.makeText(this, getString(R.string.error_alarm_past_time), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
    // </editor-fold>
}