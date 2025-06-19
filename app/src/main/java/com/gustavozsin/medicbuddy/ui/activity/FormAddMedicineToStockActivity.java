package com.gustavozsin.medicbuddy.ui.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gustavozsin.medicbuddy.R;
import com.gustavozsin.medicbuddy.dao.MedicBuddyDatabase;
import com.gustavozsin.medicbuddy.model.Medicine;
import com.gustavozsin.medicbuddy.ui.viewModel.MedicinesViewModel;

import java.util.Calendar;

public class FormAddMedicineToStockActivity extends AppCompatActivity {
    private static  String NEW_MEDICINE = "New Medicine to Stock";

    private EditText nameField;
    private EditText quantityField;
    private Spinner quantityUnitField;
    private EditText expirationDateField;
    private Spinner medicineTypeField;
    private Spinner administrationTypeField;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_add_medicine_to_stock);
        setTitle(NEW_MEDICINE);

        initializeFields();

        configureQuantityUnitSpinner();
        configureMedicineTypeSpinner();
        configureAdministrationTypeSpinner();
        configureExpirationDatePicker();

        configureSaveButton();
    }

    private void initializeFields() {
        nameField = findViewById(R.id.activity_form_add_medicine_to_stock_name);
        quantityField = findViewById(R.id.activity_form_add_medicine_to_stock_quantity_stock);
        quantityUnitField = findViewById(R.id.activity_form_add_medicine_to_stock_medicine_unit);
        expirationDateField = findViewById(R.id.activity_form_add_medicine_to_stock_expiration_date);
        medicineTypeField = findViewById(R.id.activity_form_add_medicine_to_stock_medicine_type);
        administrationTypeField = findViewById(R.id.activity_form_add_medicine_to_stock_administration_type);
        saveButton = findViewById(R.id.button_save);
    }

    private void configureQuantityUnitSpinner() {
        String[] doseUnits = getResources().getStringArray(R.array.dose_units);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doseUnits);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantityUnitField.setAdapter(adapter);
    }
    private void configureMedicineTypeSpinner() {
        String[] medicineTypes = getResources().getStringArray(R.array.medicine_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, medicineTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicineTypeField.setAdapter(adapter);
    }
    private void configureAdministrationTypeSpinner() {
        String[] administrationTypes = getResources().getStringArray(R.array.administration_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, administrationTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        administrationTypeField.setAdapter(adapter);
    }

    private void configureExpirationDatePicker() {
        expirationDateField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        expirationDateField.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }
    private void configureSaveButton() {
        saveButton.setOnClickListener(v -> {
            Medicine newMedicine = createMedicineStock();
            if (validateFields(newMedicine)) {
                saveMedicine(newMedicine);
            }
        });
    }
    private Medicine createMedicineStock() {
        String name = nameField.getText().toString();
        String quantity = quantityField.getText().toString();
        String quantityUnit = quantityUnitField.getSelectedItem().toString();
        String expirationDate = expirationDateField.getText().toString();
        String medicineType = medicineTypeField.getSelectedItem().toString();
        String administrationType = administrationTypeField.getSelectedItem().toString();

        return new Medicine(name, quantity, quantityUnit, expirationDate, medicineType, administrationType);
    }
    private boolean validateFields(Medicine medicine) {
        if (medicine.getName().trim().isEmpty() ||
                medicine.getQuantity().trim().isEmpty() ||
                medicine.getExpiration_date().trim().isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void saveMedicine(Medicine medicine) {
        MedicBuddyDatabase db = MedicBuddyDatabase.getInstance(this);
        db.medicineDAO().insert(medicine);

        // Atualiza o ViewModel compartilhado para refletir a mudança no fragmento
        MedicinesViewModel viewModel = new ViewModelProvider(
            (androidx.lifecycle.ViewModelStoreOwner) getApplication()
        ).get(MedicinesViewModel.class);
        viewModel.loadMedicines(db.medicineDAO());

        setResult(RESULT_OK);
        finish();
    }
}