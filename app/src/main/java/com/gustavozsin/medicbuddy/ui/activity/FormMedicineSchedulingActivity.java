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

    public static final String NEW_MEDICINE_SCHEDULING = "New Medicine Scheduling";
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
        setTitle(NEW_MEDICINE_SCHEDULING);

        initializeFields();

        configureNameSpinner();
        configureDoseUnitSpinner();
        configureFrequencySpinner();
        configureDatePicker();
        configureTimePicker();

        configureSaveButton();
    }

    private void configureSaveButton() {
        saveButton.setOnClickListener(v -> {
            MedicineScheduling newMedicineScheduling = createMedicineScheduling();
            if (validateFields(newMedicineScheduling)) {
                saveScheduling(newMedicineScheduling);
            }
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

    private boolean validateFields(MedicineScheduling scheduling) {
        if (scheduling.getName().trim().isEmpty() ||
                scheduling.getDose().trim().isEmpty() ||
                scheduling.getStartDate().trim().isEmpty() ||
                scheduling.getFirstDoseHour().trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.fill_in_all_required_fields), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveScheduling(MedicineScheduling newMedicineScheduling) {
        try {
            MedicBuddyDatabase db = MedicBuddyDatabase.getInstance(this);
            db.medicineSchedulingDAO().insert(newMedicineScheduling);

            MedicineSchedulingViewModel viewModel = new ViewModelProvider(this).get(MedicineSchedulingViewModel.class);
            viewModel.loadSchedulings(db.medicineSchedulingDAO());

            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.error_save_scheduling_try_again), Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private MedicineScheduling createMedicineScheduling() {
        String name = nameField.getSelectedItem().toString();
        String dose = medicineDoseField.getText().toString();
        String doseUnit = doseUnitField.getSelectedItem().toString();
        String startDate = startDateField.getText().toString();
        String frequency = frequencyField.getSelectedItem().toString();
        String firstDoseHour = firstDoseHourField.getText().toString();

        return new MedicineScheduling(name, dose, doseUnit, startDate, frequency, firstDoseHour);
    }

    private void configureNameSpinner() {
        MedicBuddyDatabase db = MedicBuddyDatabase.getInstance(this);
        List<String> medicineNames = db.medicineDAO().getAllMedicineNames();
        if (medicineNames == null || medicineNames.isEmpty()) {
            medicineNames = new java.util.ArrayList<>();
            medicineNames.add(getResources().getString(R.string.no_medicines_found));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, medicineNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameField.setAdapter(adapter);
    }
    private void configureDoseUnitSpinner() {
        String[] doseUnits = getResources().getStringArray(R.array.dose_units);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doseUnits);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doseUnitField.setAdapter(adapter);
    }

    private void configureFrequencySpinner() {
        String[] frequencies = getResources().getStringArray(R.array.frequencies);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, frequencies);
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