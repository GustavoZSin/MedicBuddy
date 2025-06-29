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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    private EditText nameField, quantityField, expirationDateField;
    private Spinner quantityUnitField, medicineTypeField, administrationTypeField;
    private Button saveButton, photoButton;
    private ImageView photoView;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_add_medicine_to_stock);

        setupToolbar();
        setTitle(getString(R.string.new_medicine));
        bindFields();
        setupSpinners();
        setupExpirationDatePicker();
        setupSaveButton();
        setupPhotoButton();
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
        saveButton = findViewById(R.id.button_save);
        photoView = findViewById(R.id.activity_form_add_medicine_to_stock_photo);
        photoButton = findViewById(R.id.activity_form_add_medicine_to_stock_btn_photo);
    }

    private void setupSpinners() {
        setSpinnerAdapter(quantityUnitField, R.array.dose_units);
        setSpinnerAdapter(medicineTypeField, R.array.medicine_types);
        setSpinnerAdapter(administrationTypeField, R.array.administration_types);
    }

    private void setSpinnerAdapter(Spinner spinner, int arrayResId) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(arrayResId));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupExpirationDatePicker() {
        expirationDateField.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
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
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            Medicine medicine = getMedicineFromFields();
            if (validateFields(medicine)) saveMedicine(medicine);
        });
    }

    private void setupPhotoButton() {
        photoButton.setOnClickListener(v -> dispatchTakePictureIntent());
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
            new ViewModelProvider(this).get(MedicinesViewModel.class).loadMedicines(db.medicineDAO());
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_save_medicine_try_again), Toast.LENGTH_LONG).show();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFileSafely();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 101);
            }
        }
    }

    private File createImageFileSafely() {
        try {
            return createImageFile();
        } catch (IOException ex) {
            return null;
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
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
