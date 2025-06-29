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
    private Integer durationDays;

    public MedicineScheduling() {}

    public MedicineScheduling(String name, String dose, String doseUnit, String startDate, String frequency, String firstDoseHour, Integer durationDays) {
        this.name = name;
        this.dose = dose;
        this.doseUnit = doseUnit;
        this.startDate = startDate;
        this.frequency = frequency;
        this.firstDoseHour = firstDoseHour;
        this.durationDays = durationDays;
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

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    @Override
    public String toString() {
        String name = this.name != null ? this.name : "";
        String hour = this.firstDoseHour != null ? this.firstDoseHour : "";
        return String.format("%s - %s", name, hour).trim();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}