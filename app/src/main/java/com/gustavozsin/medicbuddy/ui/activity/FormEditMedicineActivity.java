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
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.gustavozsin.medicbuddy.R;
import com.gustavozsin.medicbuddy.dao.MedicBuddyDatabase;
import com.gustavozsin.medicbuddy.model.Medicine;
import com.gustavozsin.medicbuddy.ui.viewModel.MedicinesViewModel;

public class FormEditMedicineActivity extends AppCompatActivity {
    private EditText nameField, quantityField, expirationDateField;
    private Spinner quantityUnitField, medicineTypeField, administrationTypeField;
    private ImageView photoView;
    private Button saveButton;
    private String currentPhotoPath;
    private int editingMedicineId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_add_medicine_to_stock);

        setupToolbar();
        setTitle(getString(R.string.edit_medicine));
        bindFields();

        Intent intent = getIntent();
        if (intent != null) {
            editingMedicineId = intent.getIntExtra("medicine_id", -1);
            loadMedicineForEdit(editingMedicineId);
        }

        saveButton.setOnClickListener(v -> {
            Medicine updatedMedicine = getMedicineFromFields();
            if (validateFields(updatedMedicine)) saveMedicine(updatedMedicine);
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);
    }

    private void bindFields() {
        nameField = findViewById(R.id.activity_form_add_medicine_to_stock_name);
        quantityField = findViewById(R.id.activity_form_add_medicine_to_stock_quantity_stock);
        quantityUnitField = findViewById(R.id.activity_form_add_medicine_to_stock_medicine_unit);
        expirationDateField = findViewById(R.id.activity_form_add_medicine_to_stock_expiration_date);
        medicineTypeField = findViewById(R.id.activity_form_add_medicine_to_stock_medicine_type);
        administrationTypeField = findViewById(R.id.activity_form_add_medicine_to_stock_administration_type);
        photoView = findViewById(R.id.activity_form_add_medicine_to_stock_photo);
        saveButton = findViewById(R.id.button_save);
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
            setupSpinnersIfNeeded();
            setSpinnerSelection(quantityUnitField, medicine.getMedicine_unit());
            setSpinnerSelection(medicineTypeField, medicine.getType());
            setSpinnerSelection(administrationTypeField, medicine.getAdministration_type());
            if (!TextUtils.isEmpty(medicine.getPhotoPath())) {
                photoView.setImageBitmap(BitmapFactory.decodeFile(medicine.getPhotoPath()));
                currentPhotoPath = medicine.getPhotoPath();
            }
        }
    }

    private void setupSpinnersIfNeeded() {
        setupSpinnerIfNeeded(quantityUnitField, R.array.dose_units);
        setupSpinnerIfNeeded(medicineTypeField, R.array.medicine_types);
        setupSpinnerIfNeeded(administrationTypeField, R.array.administration_types);
    }

    private void setupSpinnerIfNeeded(Spinner spinner, int arrayResId) {
        if (spinner.getAdapter() == null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item,
                    getResources().getStringArray(arrayResId));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
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

    private Medicine getMedicineFromFields() {
        Medicine medicine = new Medicine(
                nameField.getText().toString(),
                quantityField.getText().toString(),
                quantityUnitField.getSelectedItem().toString(),
                expirationDateField.getText().toString(),
                medicineTypeField.getSelectedItem().toString(),
                administrationTypeField.getSelectedItem().toString()
        );
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
            new ViewModelProvider(this).get(MedicinesViewModel.class).loadMedicines(db.medicineDAO());
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_save_medicine_try_again), Toast.LENGTH_LONG).show();
        }
    }
}
