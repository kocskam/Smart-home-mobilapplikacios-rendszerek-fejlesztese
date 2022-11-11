package com.example.project.model;

public class Course {

    public final int id;
    public final String name;

    public Course(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
