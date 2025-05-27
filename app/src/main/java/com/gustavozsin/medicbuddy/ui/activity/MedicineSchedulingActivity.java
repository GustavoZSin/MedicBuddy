package com.gustavozsin.medicbuddy.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gustavozsin.medicbuddy.R;
import com.gustavozsin.medicbuddy.dao.MedicineSchedulingDAO;
import com.gustavozsin.medicbuddy.model.MedicineScheduling;
import com.gustavozsin.medicbuddy.ui.fragment.HomeFragment;

import java.util.Calendar;

public class MedicineSchedulingActivity extends AppCompatActivity {

    private static final String[] DOSE_UNITS = {"Pill", "Tablet", "mL", "Drop", "Injection"};
    private static final String[] FREQUENCIES = {"Every 4 hours", "Every 6 hours", "Every 8 hours", "Every 12 hours", "Once a day"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_scheduling);

        final EditText nameField = findViewById(R.id.activity_medicine_scheduling_medicine_name);
        final EditText medicineDoseField = findViewById(R.id.activity_medicine_scheduling_medicine_dose);
        final Spinner doseUnitField = findViewById(R.id.activity_medicine_scheduling_medicine_dose_unit);
        final EditText startDateField = findViewById(R.id.activity_medicine_scheduling_medicine_start_date);
        final Spinner frequencyField = findViewById(R.id.activity_medicine_scheduling_medicine_frequency);
        final EditText firstDoseHourField = findViewById(R.id.activity_medicine_scheduling_medicine_first_dose_hour);
        final Button saveButton = findViewById(R.id.activity_medicine_scheduling_button_save);

        configureDoseUnitSpinner(doseUnitField);
        configureFrequencySpinner(frequencyField);
        configureDatePicker(startDateField);
        configureTimePicker(firstDoseHourField);

        saveButton.setOnClickListener(v -> {
            String name = nameField.getText().toString();
            String dose = medicineDoseField.getText().toString();
            String doseUnit = doseUnitField.getSelectedItem().toString();
            String startDate = startDateField.getText().toString();
            String frequency = frequencyField.getSelectedItem().toString();
            String firstDoseHour = firstDoseHourField.getText().toString();

            MedicineScheduling newMedicineScheduling =
                    new MedicineScheduling(name, dose, doseUnit, startDate, frequency, firstDoseHour);

            MedicineSchedulingDAO dao = new MedicineSchedulingDAO();
            dao.save(newMedicineScheduling);

            startActivity(new Intent(MedicineSchedulingActivity.this, MainActivity.class));
            finish();
        });
    }

    private void configureDoseUnitSpinner(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DOSE_UNITS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void configureFrequencySpinner(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, FREQUENCIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void configureDatePicker(EditText dateField) {
        dateField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        dateField.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void configureTimePicker(EditText timeField) {
        timeField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(
                    this,
                    (TimePicker view, int hourOfDay, int minute) -> {
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        timeField.setText(selectedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            ).show();
        });
    }
}
