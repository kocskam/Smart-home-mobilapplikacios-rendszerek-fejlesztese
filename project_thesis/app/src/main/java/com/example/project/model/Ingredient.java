package com.example.project.model;

public class Ingredient {

    private String name;
    private float amount;
    private String unit;
    private int importance;

    public Ingredient(String name, float amount, String unit, int importance) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.importance = importance;
    }

    public Ingredient() {

    }

    public String getName() {
        return name;
    }

    public float getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public int getImportance() {
        return importance;
    }

    @Override
    public String toString() {
        return amount + " " + unit + " " + name;
    }

}
