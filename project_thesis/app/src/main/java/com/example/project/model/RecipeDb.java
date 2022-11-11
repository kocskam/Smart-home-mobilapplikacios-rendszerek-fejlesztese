package com.example.project.model;

import java.util.List;

public class RecipeDb {

    private Recipe recipe;
    private List<Step> steps;
    private List<Ingredient> ingredients;

    public RecipeDb(Recipe recipe, List<Step> steps, List<Ingredient> ingredients) {
        this.recipe = recipe;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public String toString() {
        return "RecipeDb{" +
                "recipe=" + recipe +
                ", steps=" + steps +
                ", ingredients=" + ingredients +
                '}';
    }

}
