package com.gustavozsin.medicbuddy.model;

public class MedicineScheduling {
    private final String name;
    private final String dose;
    private final String doseUnit;
    private final String startDate;
    private final String frequency;
    private final String firstDoseHour;

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

    public String getDose() {
        return dose;
    }

    public String getDoseUnit() {
        return doseUnit;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getFirstDoseHour() {
        return firstDoseHour;
    }

    @Override
    public String toString() {
        return name;
    }
}
