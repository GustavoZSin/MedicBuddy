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
import androidx.appcompat.widget.Toolbar;
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
    private EditText frequencyField; // agora EditText
    private EditText firstDoseHourField;
    private EditText durationDaysField;
    private EditText endDateTimeField;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_scheduling);

        setupToolbar();
        setTitle(getString(R.string.new_medicine_scheduling));
        initializeFields();
        setupSpinners();
        setupPickers();
        setupSaveButton();
    }

    // <editor-fold desc="Setups">
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);
    }

    private void initializeFields() {
        nameField = findViewById(R.id.activity_medicine_scheduling_medicine_name);
        medicineDoseField = findViewById(R.id.activity_medicine_scheduling_medicine_dose);
        doseUnitField = findViewById(R.id.activity_medicine_scheduling_medicine_dose_unit);
        startDateField = findViewById(R.id.activity_medicine_scheduling_medicine_start_date);
        frequencyField = findViewById(R.id.activity_medicine_scheduling_medicine_frequency); // agora EditText
        firstDoseHourField = findViewById(R.id.activity_medicine_scheduling_medicine_first_dose_hour);
        durationDaysField = findViewById(R.id.activity_medicine_scheduling_duration_days);
        endDateTimeField = findViewById(R.id.activity_medicine_scheduling_end_datetime);
        saveButton = findViewById(R.id.button_save);
    }

    private void setupSpinners() {
        setupNameSpinner();
        setupDoseUnitSpinner();
        // Removido: setupFrequencySpinner();

        // Removido: frequencyField.setOnItemSelectedListener...
        frequencyField.addTextChangedListener(new android.text.TextWatcher() {
            public void afterTextChanged(android.text.Editable s) { updateEndDateTimeField(); }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void setupPickers() {
        setupDatePicker();
        setupTimePicker();

        durationDaysField.addTextChangedListener(new android.text.TextWatcher() {
            public void afterTextChanged(android.text.Editable s) { updateEndDateTimeField(); }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            MedicineScheduling scheduling = createMedicineSchedulingFromFields();
            if (validateFields(scheduling)) {
                generateAndSaveSchedulings(scheduling);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void generateAndSaveSchedulings(MedicineScheduling baseScheduling) {
        try {
            int durationDays = baseScheduling.getDurationDays() != null ? baseScheduling.getDurationDays() : 1;
            int freqHours = getFrequencyHours(baseScheduling.getFrequency());
            String[] dateParts = baseScheduling.getStartDate().split("/");
            String[] timeParts = baseScheduling.getFirstDoseHour().split(":");

            Calendar cal = Calendar.getInstance();
            cal.set(
                Integer.parseInt(dateParts[2]),
                Integer.parseInt(dateParts[1]) - 1,
                Integer.parseInt(dateParts[0]),
                Integer.parseInt(timeParts[0]),
                Integer.parseInt(timeParts[1]),
                0
            );

            Calendar endCal = (Calendar) cal.clone();
            endCal.add(Calendar.DATE, durationDays);

            MedicBuddyDatabase db = MedicBuddyDatabase.getInstance(this);
            MedicineSchedulingViewModel viewModel = new ViewModelProvider(this).get(MedicineSchedulingViewModel.class);

            while (true) {
                Calendar compareCal = (Calendar) cal.clone();
                compareCal.set(Calendar.SECOND, 0);
                compareCal.set(Calendar.MILLISECOND, 0);

                Calendar lastPossible = (Calendar) endCal.clone();
                lastPossible.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
                lastPossible.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
                lastPossible.set(Calendar.SECOND, 0);
                lastPossible.set(Calendar.MILLISECOND, 0);

                if (compareCal.after(lastPossible)) break;

                MedicineScheduling scheduling = new MedicineScheduling(
                    baseScheduling.getName(),
                    baseScheduling.getDose(),
                    baseScheduling.getDoseUnit(),
                    String.format("%02d/%02d/%04d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)),
                    baseScheduling.getFrequency(),
                    String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)),
                    durationDays
                );
                db.medicineSchedulingDAO().insert(scheduling);
                scheduleAlarm(scheduling);

                cal.add(Calendar.HOUR_OF_DAY, freqHours);
            }
            viewModel.loadSchedulings(db.medicineSchedulingDAO());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_save_scheduling_try_again), Toast.LENGTH_LONG).show();
        }
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

    private void setupDatePicker() {
        startDateField.setOnClickListener(v -> showDatePicker());
        startDateField.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDatePicker();
        });
    }

    private void setupTimePicker() {
        firstDoseHourField.setOnClickListener(v -> showTimePicker());
        firstDoseHourField.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showTimePicker();
        });
    }

    private void showDatePicker() {
        startDateField.clearFocus();
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(startDateField.getWindowToken(), 0);

        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    startDateField.setText(selectedDate);
                    updateEndDateTimeField();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showTimePicker() {
        firstDoseHourField.clearFocus();
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(firstDoseHourField.getWindowToken(), 0);

        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(
                this,
                (TimePicker view, int hourOfDay, int minute) -> {
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                    firstDoseHourField.setText(selectedTime);
                    updateEndDateTimeField();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        ).show();
    }

    private void updateEndDateTimeField() {
        String startDate = startDateField.getText().toString();
        String firstDoseHour = firstDoseHourField.getText().toString();
        String durationStr = durationDaysField.getText().toString();

        if (startDate.isEmpty() || firstDoseHour.isEmpty() || durationStr.isEmpty()) {
            endDateTimeField.setText("");
            return;
        }

        try {
            int days = Integer.parseInt(durationStr);

            String[] dateParts = startDate.split("/");
            String[] timeParts = firstDoseHour.split(":");

            Calendar cal = Calendar.getInstance();
            cal.set(
                Integer.parseInt(dateParts[2]),
                Integer.parseInt(dateParts[1]) - 1,
                Integer.parseInt(dateParts[0]),
                Integer.parseInt(timeParts[0]),
                Integer.parseInt(timeParts[1]),
                0
            );

            cal.add(Calendar.DATE, days);

            String endDate = String.format("%02d/%02d/%04d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
            String endTime = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            endDateTimeField.setText(endDate + " " + endTime);
        } catch (Exception e) {
            endDateTimeField.setText("");
        }
    }

    private int getFrequencyHours(String frequency) {
        // frequency agora é o valor digitado no EditText
        try {
            if (frequency == null || frequency.trim().isEmpty()) return 24;
            return Integer.parseInt(frequency.trim());
        } catch (NumberFormatException e) {
            return 24;
        }
    }
    // </editor-fold>

    // <editor-fold desc="Criação, Validação, Agendamentos, Banco de Dados">
    @NonNull
    private MedicineScheduling createMedicineSchedulingFromFields() {
        String name = nameField.getSelectedItem().toString();
        String dose = medicineDoseField.getText().toString();
        String doseUnit = doseUnitField.getSelectedItem().toString();

        String startDate = startDateField.getText().toString();
        String frequency = frequencyField.getText().toString(); // agora EditText
        String firstDoseHour = firstDoseHourField.getText().toString();

        String auxDurationDays = durationDaysField.getText().toString();
        Integer durationDays = auxDurationDays.isEmpty() ? 0 : Integer.parseInt(auxDurationDays);

        MedicineScheduling scheduling = new MedicineScheduling(name, dose, doseUnit, startDate, frequency, firstDoseHour, durationDays);
        scheduling.setDurationDays(durationDays);
        return scheduling;
    }

    private boolean validateFields(MedicineScheduling scheduling) {
        if (scheduling.getName().trim().isEmpty() ||
                scheduling.getDose().trim().isEmpty() ||
                scheduling.getStartDate().trim().isEmpty() ||
                scheduling.getFirstDoseHour().trim().isEmpty() ||
                scheduling.getDurationDays() == null ||
                scheduling.getFrequency().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_in_all_required_fields), Toast.LENGTH_SHORT).show();
            return false;
        }
        // Frequência deve ser um inteiro positivo
        try {
            int freq = Integer.parseInt(scheduling.getFrequency().trim());
            if (freq <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.invalid_frequency), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

                int requestCode = getUniqueRequestCode(scheduling);
                android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                        this, requestCode, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
                );

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getUniqueRequestCode(MedicineScheduling scheduling) {
        String key = scheduling.getName() + "_" + scheduling.getStartDate() + "_" + scheduling.getFirstDoseHour();
        return key.hashCode();
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