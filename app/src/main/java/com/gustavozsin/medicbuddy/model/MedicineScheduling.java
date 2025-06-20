package com.gustavozsin.medicbuddy.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MedicineScheduling {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String dose;
    private String doseUnit;
    private String startDate;
    private String frequency;
    private String firstDoseHour;

    public MedicineScheduling() {}

    public MedicineScheduling(String name, String dose, String doseUnit, String startDate, String frequency, String firstDoseHour) {
        this.name = name;
        this.dose = dose;
        this.doseUnit = doseUnit;
        this.startDate = startDate;
        this.frequency = frequency;
        this.firstDoseHour = firstDoseHour;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) { this.name = name; }

    public String getDose() {
        return dose;
    }
    public void setDose(String dose) { this.dose = dose; }

    public String getDoseUnit() {
        return doseUnit;
    }
    public void setDoseUnit(String doseUnit) { this.doseUnit = doseUnit; }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getFrequency() {
        return frequency;
    }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getFirstDoseHour() {
        return firstDoseHour;
    }
    public void setFirstDoseHour(String firstDoseHour) { this.firstDoseHour = firstDoseHour; }

    @Override
    public String toString() {
        return name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
