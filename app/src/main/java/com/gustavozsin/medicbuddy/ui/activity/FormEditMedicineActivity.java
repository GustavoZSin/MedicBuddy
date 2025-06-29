package com.gustavozsin.medicbuddy.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gustavozsin.medicbuddy.R;
import com.gustavozsin.medicbuddy.dao.MedicBuddyDatabase;
import com.gustavozsin.medicbuddy.model.Medicine;
import com.gustavozsin.medicbuddy.ui.viewModel.MedicinesViewModel;

public class FormEditMedicineActivity extends AppCompatActivity {
    private EditText nameField;
    private EditText quantityField;
    private Spinner quantityUnitField;
    private EditText expirationDateField;
    private Spinner medicineTypeField;
    private Spinner administrationTypeField;
    private ImageView photoView;
    private Button saveButton;
    private String currentPhotoPath;
    private int editingMedicineId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_add_medicine_to_stock);

        setTitle(getString(R.string.edit_medicine));

        nameField = findViewById(R.id.activity_form_add_medicine_to_stock_name);
        quantityField = findViewById(R.id.activity_form_add_medicine_to_stock_quantity_stock);
        quantityUnitField = findViewById(R.id.activity_form_add_medicine_to_stock_medicine_unit);
        expirationDateField = findViewById(R.id.activity_form_add_medicine_to_stock_expiration_date);
        medicineTypeField = findViewById(R.id.activity_form_add_medicine_to_stock_medicine_type);
        administrationTypeField = findViewById(R.id.activity_form_add_medicine_to_stock_administration_type);
        photoView = findViewById(R.id.activity_form_add_medicine_to_stock_photo);
        saveButton = findViewById(R.id.button_save);

        Intent intent = getIntent();
        if (intent != null) {
            editingMedicineId = intent.getIntExtra("medicine_id", -1);
            loadMedicineForEdit(editingMedicineId);
        }

        saveButton.setOnClickListener(v -> {
            Medicine updatedMedicine = createMedicineFromFields();
            if (validateFields(updatedMedicine)) {
                saveMedicine(updatedMedicine);
            }
        });
    }

    private void loadMedicineForEdit(int medicineId) {
        if (medicineId == -1) return;
        MedicBuddyDatabase db = MedicBuddyDatabase.getInstance(this);
        Medicine medicine = null;
        for (Medicine m : db.medicineDAO().getAll()) {
            if (m.getId() == medicineId) {
                medicine = m;
                break;
            }
        }
        if (medicine != null) {
            nameField.setText(medicine.getName());
            quantityField.setText(medicine.getQuantity());
            expirationDateField.setText(medicine.getExpiration_date());

            if (quantityUnitField.getAdapter() == null) {
                String[] doseUnits = getResources().getStringArray(R.array.dose_units);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doseUnits);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                quantityUnitField.setAdapter(adapter);
            }
            if (medicineTypeField.getAdapter() == null) {
                String[] medicineTypes = getResources().getStringArray(R.array.medicine_types);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, medicineTypes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                medicineTypeField.setAdapter(adapter);
            }
            if (administrationTypeField.getAdapter() == null) {
                String[] administrationTypes = getResources().getStringArray(R.array.administration_types);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, administrationTypes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                administrationTypeField.setAdapter(adapter);
            }

            var unit = medicine.getMedicine_unit();

            setSpinnerSelection(quantityUnitField, unit);
            setSpinnerSelection(medicineTypeField, medicine.getType());
            setSpinnerSelection(administrationTypeField, medicine.getAdministration_type());
            if (!TextUtils.isEmpty(medicine.getPhotoPath())) {
                photoView.setImageBitmap(BitmapFactory.decodeFile(medicine.getPhotoPath()));
                currentPhotoPath = medicine.getPhotoPath();
            }
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);
            if (item != null && value.trim().equalsIgnoreCase(item.toString().trim())) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private Medicine createMedicineFromFields() {
        String name = nameField.getText().toString();
        String quantity = quantityField.getText().toString();
        String quantityUnit = quantityUnitField.getSelectedItem().toString();
        String expirationDate = expirationDateField.getText().toString();
        String medicineType = medicineTypeField.getSelectedItem().toString();
        String administrationType = administrationTypeField.getSelectedItem().toString();

        Medicine medicine = new Medicine(name, quantity, quantityUnit, expirationDate, administrationType, medicineType);
        medicine.setId(editingMedicineId);
        medicine.setPhotoPath(currentPhotoPath);
        return medicine;
    }

    private boolean validateFields(Medicine medicine) {
        if (medicine.getName().trim().isEmpty() ||
                medicine.getQuantity().trim().isEmpty() ||
                medicine.getExpiration_date().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_in_all_required_fields), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveMedicine(Medicine medicine) {
        try {
            MedicBuddyDatabase db = MedicBuddyDatabase.getInstance(this);
            db.medicineDAO().update(medicine);

            MedicinesViewModel viewModel = new ViewModelProvider(this).get(MedicinesViewModel.class);
            viewModel.loadMedicines(db.medicineDAO());

            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_save_medicine_try_again), Toast.LENGTH_LONG).show();
        }
    }
}
