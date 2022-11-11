package com.example.project.model;

public class Importance {

    public final int value;
    public final String description;

    public Importance(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Importance{" +
                "value=" + value +
                ", description='" + description + '\'' +
                '}';
    }

}
