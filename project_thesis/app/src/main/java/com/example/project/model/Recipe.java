package com.example.project.model;

public class Recipe {

    private Integer id;
    private String name;
    private Integer cookTime;
    private String description;
    private Course course;
    private FoodCategory foodCategory;

    public Recipe(Integer id, String name, int cookTime, String description, Course course, FoodCategory foodCategory) {
        this.id = id;
        this.name = name;
        this.cookTime = cookTime;
        this.course = course;
        this.foodCategory = foodCategory;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCookTime() {
        return cookTime;
    }

    public Course getCourse() {
        return course;
    }

    public FoodCategory getFoodCategory() {
        return foodCategory;
    }

    public String getDescription() {
        return description;
    }


    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cookTime=" + cookTime +
                ", description='" + description + '\'' +
                ", course=" + course +
                ", foodCategory=" + foodCategory +
                '}';
    }

}
