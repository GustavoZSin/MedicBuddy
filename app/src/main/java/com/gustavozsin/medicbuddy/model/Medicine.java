package com.gustavozsin.medicbuddy.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Medicine {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String quantity;
    private String medicine_unit;
    private String expiration_date;
    private String administration_type;
    private String type;
    private String photoPath;

    public Medicine(String name, String quantity, String medicine_unit, String expiration_date, String administration_type, String type) {
        this.name = name;
        this.quantity = quantity;
        this.medicine_unit = medicine_unit;
        this.expiration_date = expiration_date;
        this.administration_type = administration_type;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getMedicine_unit() { return medicine_unit; }
    public void setMedicine_unit(String medicine_unit) { this.medicine_unit = medicine_unit; }

    public String getExpiration_date() { return expiration_date; }
    public void setExpiration_date(String expiration_date) { this.expiration_date = expiration_date; }

    public String getAdministration_type() { return administration_type; }
    public void setAdministration_type(String administration_type) { this.administration_type = administration_type; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getMedicineWithQuantity() {
        String name = this.name != null ? this.name : "";
        String quantity = this.quantity != null ? this.quantity : "";
        return String.format("%s (%s)", name, quantity).trim();
    }
}