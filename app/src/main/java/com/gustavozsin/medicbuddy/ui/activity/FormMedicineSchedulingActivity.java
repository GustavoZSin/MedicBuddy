package com.gustavozsin.medicbuddy.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gustavozsin.medicbuddy.R;
import com.gustavozsin.medicbuddy.dao.MedicineSchedulingDAO;
import com.gustavozsin.medicbuddy.model.MedicineScheduling;

import java.util.Calendar;

public class FormMedicineSchedulingActivity extends AppCompatActivity {

    public static final String NEW_MEDICINE_SCHEDULING = "New Medicine Scheduling";
    private EditText nameField;
    private EditText medicineDoseField;
    private Spinner doseUnitField;
    private EditText startDateField;
    private Spinner frequencyField;
    private EditText firstDoseHourField;
    private Button saveButton;
    private final MedicineSchedulingDAO dao = new MedicineSchedulingDAO();
    private static final String[] DOSE_UNITS = {"Pill", "Tablet", "mL", "Drop", "Injection"};
    private static final String[] FREQUENCIES = {"Every 4 hours", "Every 6 hours", "Every 8 hours", "Every 12 hours", "Once a day"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_scheduling);
        setTitle(NEW_MEDICINE_SCHEDULING);

        initializeFields();

        configureDoseUnitSpinner();
        configureFrequencySpinner();
        configureDatePicker();
        configureTimePicker();

        configureSaveButton();
    }

    private void configureSaveButton() {
        saveButton.setOnClickListener(v -> {
            MedicineScheduling newMedicineScheduling = createMedicineScheduling();
            saveScheduling(newMedicineScheduling);
        });
    }

    private void initializeFields() {
        nameField = findViewById(R.id.activity_medicine_scheduling_medicine_name);
        medicineDoseField = findViewById(R.id.activity_medicine_scheduling_medicine_dose);
        doseUnitField = findViewById(R.id.activity_medicine_scheduling_medicine_dose_unit);
        startDateField = findViewById(R.id.activity_medicine_scheduling_medicine_start_date);
        frequencyField = findViewById(R.id.activity_medicine_scheduling_medicine_frequency);
        firstDoseHourField = findViewById(R.id.activity_medicine_scheduling_medicine_first_dose_hour);
        saveButton = findViewById(R.id.button_save);
    }

    private void saveScheduling(MedicineScheduling newMedicineScheduling) {
        dao.save(newMedicineScheduling);
        finish();
    }

    @NonNull
    private MedicineScheduling createMedicineScheduling() {
        String name = nameField.getText().toString();
        String dose = medicineDoseField.getText().toString();
        String doseUnit = doseUnitField.getSelectedItem().toString();
        String startDate = startDateField.getText().toString();
        String frequency = frequencyField.getSelectedItem().toString();
        String firstDoseHour = firstDoseHourField.getText().toString();

        return new MedicineScheduling(name, dose, doseUnit, startDate, frequency, firstDoseHour);
    }

    private void configureDoseUnitSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DOSE_UNITS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doseUnitField.setAdapter(adapter);
    }

    private void configureFrequencySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, FREQUENCIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencyField.setAdapter(adapter);
    }

    private void configureDatePicker() {
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

    private void configureTimePicker() {
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
}
