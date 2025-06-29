package com.gustavozsin.medicbuddy.ui.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.gustavozsin.medicbuddy.R;
import com.gustavozsin.medicbuddy.dao.MedicBuddyDatabase;
import com.gustavozsin.medicbuddy.model.Medicine;
import com.gustavozsin.medicbuddy.ui.viewModel.MedicinesViewModel;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class FormAddMedicineToStockActivity extends AppCompatActivity {
    private EditText nameField;
    private EditText quantityField;
    private Spinner quantityUnitField;
    private EditText expirationDateField;
    private Spinner medicineTypeField;
    private Spinner administrationTypeField;
    private Button saveButton;
    private ImageView photoView;
    private Button photoButton;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_add_medicine_to_stock);

        String newMedicineTitle = getString(R.string.new_medicine);
        setTitle(newMedicineTitle);

        initializeFields();
        setupSpinners();
        setupPickers();
        setupSaveButton();

        photoButton.setOnClickListener(v -> dispatchTakePictureIntent());
    }

    // <editor-fold desc="Setups">
    private void initializeFields() {
        nameField = findViewById(R.id.activity_form_add_medicine_to_stock_name);
        quantityField = findViewById(R.id.activity_form_add_medicine_to_stock_quantity_stock);
        quantityUnitField = findViewById(R.id.activity_form_add_medicine_to_stock_medicine_unit);
        expirationDateField = findViewById(R.id.activity_form_add_medicine_to_stock_expiration_date);
        medicineTypeField = findViewById(R.id.activity_form_add_medicine_to_stock_medicine_type);
        administrationTypeField = findViewById(R.id.activity_form_add_medicine_to_stock_administration_type);
        saveButton = findViewById(R.id.button_save);
        photoView = findViewById(R.id.activity_form_add_medicine_to_stock_photo);
        photoButton = findViewById(R.id.activity_form_add_medicine_to_stock_btn_photo);
    }

    private void setupSpinners() {
        setupQuantityUnitSpinner();
        setupMedicineTypeSpinner();
        setupAdministrationTypeSpinner();
    }

    private void setupPickers() {
        setupExpirationDatePicker();
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            Medicine newMedicine = createMedicineStock();
            if (validateFields(newMedicine)) {
                saveMedicine(newMedicine);
            }
        });
    }

    private void setupQuantityUnitSpinner() {
        String[] doseUnits = getResources().getStringArray(R.array.dose_units);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doseUnits);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantityUnitField.setAdapter(adapter);
    }

    private void setupMedicineTypeSpinner() {
        String[] medicineTypes = getResources().getStringArray(R.array.medicine_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, medicineTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicineTypeField.setAdapter(adapter);
    }

    private void setupAdministrationTypeSpinner() {
        String[] administrationTypes = getResources().getStringArray(R.array.administration_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, administrationTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        administrationTypeField.setAdapter(adapter);
    }

    private void setupExpirationDatePicker() {
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
    // </editor-fold>

    // <editor-fold desc="Criação, Validação, Banco de Dados">
    private Medicine createMedicineStock() {
        String name = nameField.getText().toString();
        String quantity = quantityField.getText().toString();
        String quantityUnit = quantityUnitField.getSelectedItem().toString();
        String expirationDate = expirationDateField.getText().toString();
        String medicineType = medicineTypeField.getSelectedItem().toString();
        String administrationType = administrationTypeField.getSelectedItem().toString();

        Medicine medicine = new Medicine(name, quantity, quantityUnit, expirationDate, medicineType, administrationType);
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
            db.medicineDAO().insert(medicine);

            MedicinesViewModel viewModel = new ViewModelProvider(this).get(MedicinesViewModel.class);
            viewModel.loadMedicines(db.medicineDAO());

            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_save_medicine_try_again), Toast.LENGTH_LONG).show();
        }
    }
    // </editor-fold>

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 101);
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            photoView.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
        }
    }
}
