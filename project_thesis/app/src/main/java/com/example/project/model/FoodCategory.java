package com.example.project.model;

public class FoodCategory {

    public final int id;
    public final String name;

    public FoodCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "FoodCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}



