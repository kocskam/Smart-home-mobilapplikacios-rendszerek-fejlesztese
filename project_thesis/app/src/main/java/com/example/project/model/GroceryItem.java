package com.example.project.model;

import org.jetbrains.annotations.NotNull;

public class GroceryItem {

    public Integer id;
    public final String name;
    public final float amount;
    public final String unit;
    public final String description;

    public GroceryItem(Integer id, @NotNull String name, float amount, String unit, String description) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.description = description;
    }

    @Override
    public String toString() {
        return "GroceryItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", unit='" + unit + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
