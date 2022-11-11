package com.example.project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.project.model.Course;
import com.example.project.model.FoodCategory;
import com.example.project.model.Importance;
import com.example.project.model.Ingredient;
import com.example.project.model.Recipe;
import com.example.project.model.RecipeDb;
import com.example.project.model.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeDatabaseAccess {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static RecipeDatabaseAccess instance;
    Cursor c = null;

    private RecipeDatabaseAccess(Context context) {
        this.openHelper = new RecipeDatabaseHandler(context);
    }

    public static RecipeDatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new RecipeDatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.db = openHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null) {
            this.db.close();
        }
    }

    /**CRUD - Ingredient*/
    //CREATE
    public long createIngredient(String ing) {
        ContentValues cv = new ContentValues();
        cv.put("ingredient_name", ing);
        return db.insert("ingredients", null, cv);
    }

    //READ
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>();
        String selectQuery = "SELECT ingredient_name FROM ingredients";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                ingredients.add(c.getString(0));
            }
            while (c.moveToNext());
        }
        c.close();
        return ingredients;
    }

    //UPDATE
    public void updateIngredient(long id, String name) {
        ContentValues cv = new ContentValues();
        cv.put("ingredient_id", id);
        cv.put("ingredient_name", name);
        db.update("ingredients", cv, "ingredient_id = ?", new String[]{String.valueOf(id)});
    }

    //DELETE
    public void deleteIngredient(long id) {
        db.delete("ingredients", "ingredient_id = '" + id + "'", null);
    }


    /**CRUD - Recipe*/
    //CREATE
    public long createRecipe(Recipe recipe) {
        ContentValues cv = new ContentValues();
        cv.put("food_category_id", recipe.getFoodCategory().id);
        cv.put("recipe_name", recipe.getName());
        cv.put("recipe_description", recipe.getDescription());
        cv.put("cook_time", recipe.getCookTime());
        cv.put("course_id", recipe.getCourse().id);

        return db.insert("recipes", null, cv);
    }

    //READ
    public Recipe getRecipe(int recipeId) {
        Recipe recipe = null;
        String selectQuery = "" +
                "SELECT recipe_name, cook_time, recipe_description, courses.course_name, food_categories.food_category_name " +
                "FROM recipes " +
                "INNER JOIN courses ON courses.course_id = recipes.course_id " +
                "INNER JOIN food_categories ON food_categories.food_category_id = recipes.food_category_id " +
                "WHERE recipe_id = " + recipeId;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst() && c != null) {
            String courseName = c.getString(3);
            Course course = new Course(getCourseId(courseName), courseName);
            String categoryName = c.getString(4);
            FoodCategory foodCategory = new FoodCategory(getFoodCategoryId(categoryName), categoryName);
            recipe = new Recipe(recipeId, c.getString(0), Integer.parseInt(c.getString(1)), c.getString(2),
                    course, foodCategory);
        }
        c.close();
        return recipe;
    }

    //READ ALL
    public List<Recipe> getRecipesForRow() {
        List<Recipe> recipes = new ArrayList<>();
        String selectQuery = "" +
                "SELECT recipe_id, recipe_name, cook_time, recipe_description, courses.course_name, food_categories.food_category_name " +
                "FROM recipes " +
                "INNER JOIN courses ON  courses.course_id = recipes.course_id " +
                "INNER JOIN food_categories ON food_categories.food_category_id = recipes.food_category_id";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                String courseName = c.getString(4);
                String categoryName = c.getString(5);

                Recipe recipe = new Recipe(Integer.parseInt(c.getString(0)), c.getString(1),
                        Integer.parseInt(c.getString(2)), c.getString(3), new Course(getCourseId(courseName), courseName),
                        new FoodCategory(getFoodCategoryId(categoryName), categoryName));
                recipes.add(recipe);
            }
            while (c.moveToNext());
        }
        c.close();
        return recipes;
    }

    //UPDATE
    public long updateRecipe(Recipe recipe, String id) {
        ContentValues cv = new ContentValues();
        cv.put("food_category_id", recipe.getFoodCategory().id);
        cv.put("recipe_name", recipe.getName());
        cv.put("recipe_description", recipe.getDescription());
        cv.put("cook_time", recipe.getCookTime());
        cv.put("course_id", recipe.getCourse().id);

        return db.update("recipes", cv, "recipe_id = ?", new String[]{id});
    }

    //DELETE
    public void deleteRecipe(long id) {
        db.delete("recipes", "recipe_id = '" + id + "'", null);
    }


    /**CRUD - RecipesIngredients*/
    //CREATE
    public long createRecipesIngredient(long recipeId, long ingredientId, long unitId, long qtyId, long impId) {
        ContentValues cv = new ContentValues();
        cv.put("recipe_id", recipeId);
        cv.put("ingredient_id", ingredientId);
        cv.put("measurement_unit_id", unitId);
        cv.put("measurement_qty_id", qtyId);
        cv.put("importance_value", impId);
        return db.insert("recipes_ingredients", null, cv);
    }

    //DELETE
    public void deleteRecipesIngredients(long recipeId) {
        db.delete("recipes_ingredients", "recipe_id = '" + recipeId + "'", null);
    }


    /**CRUD - Steps*/
    //CREATE
    public void createSteps(List<Step> steps, long recipeId) {
        for (Step s : steps) {
            ContentValues cv = new ContentValues();
            cv.put("recipe_id", recipeId);
            cv.put("step_number",s.getStepNumber());
            cv.put("step_description",s.getStepDescription());
            db.insert("steps", null, cv);
        }
    }

    //READ ALL
    public List<Step> getSteps(int recipeId) {
        List<Step> steps = new ArrayList<>();
        String selectQuery = "SELECT step_number, step_description FROM steps WHERE recipe_id = " + recipeId + " ORDER BY step_number";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Step step = new Step(Integer.parseInt(c.getString(0)), c.getString(1));
                steps.add(step);
            }
            while (c.moveToNext());
        }
        c.close();
        return steps;
    }

    //UPDATE
    public void updateSteps(List<Step> steps, long recipeId) {
        db.delete("steps", "recipe_id = '" + recipeId + "'", null);
        createSteps(steps, recipeId);
    }

    //DELETE
    public void deleteStep(long recipeId) {
        db.delete("steps", "recipe_id = '" + recipeId + "'", null);
    }

    /**CRUD - RecipeDb*/
    //READ
    public RecipeDb getRecipeForFragment(int recipId) {
        Recipe recipe = getRecipe(recipId);
        List<Step> steps = getSteps(recipId);
        List<Ingredient> ingredients = getIngredientByRecipeId(recipId);

        return new RecipeDb(recipe, steps,  ingredients/*, importance*/);
    }

    //READ ALL
    public List<RecipeDb> getRecipeDbs() {
        return getRecipesForRow().stream()
                .map(recipe -> getRecipeForFragment(recipe.getId()))
                .collect(Collectors.toList());
    }


    /**CRUD - Courses*/
    //READ ALL
    public List<Course> getCourses() {
        List<Course> courses = new ArrayList<>();
        String selectQuery = "SELECT * FROM courses";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Course course = new Course(Integer.parseInt(c.getString(0)), c.getString(1));
                courses.add(course);
            }
            while (c.moveToNext());
        }
        c.close();
        return courses;
    }


    /**CRUD - FoodCategory*/
    //READ ALL
    public List<FoodCategory> getFoodCategories() {
        List<FoodCategory> categories = new ArrayList<>();
        String selectQuery = "SELECT * FROM food_categories";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                FoodCategory category = new FoodCategory(Integer.parseInt(c.getString(0)), c.getString(1));
                categories.add(category);
            }
            while (c.moveToNext());
        }
        c.close();
        return categories;
    }


    /**CRUD - Importance*/
    //READ ALL
    public List<Importance> getAllImportance() {
        List<Importance> importanceList = new ArrayList<>();
        String selectQuery = "SELECT * FROM importance";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                importanceList.add(new Importance(Integer.parseInt(c.getString(0)), c.getString(1)));
            }
            while (c.moveToNext());
        }
        c.close();
        return importanceList;
    }


    /**Qty*/
    //CREATE
    public long createQty(float amount) {
        ContentValues cv = new ContentValues();
        cv.put("measurement_qty_amount", amount);
        return db.insert("measurement_qty", null, cv);
    }

    //UPSERT - create if not exists
    public long upsertQty(float amount) {
        String id = "";
        String selectQuery = "SELECT measurement_qty_id FROM measurement_qty WHERE measurement_qty_amount = " + "'" + amount + "'";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                id = c.getString(0);
            }
            while (c.moveToNext());
            return Long.parseLong(id);
        }
        c.close();
        return createQty(amount);
    }


    /**Unit*/
    //CREATE
    public long createUnit(String unit) {
        ContentValues cv = new ContentValues();
        cv.put("measurement_unit_name", unit);
        return db.insert("measurement_units", null, cv);
    }

    //UPSERT
    public long upsertUnit(String name) {
        String id = "";
        String selectQuery = "SELECT measurement_unit_id FROM measurement_units WHERE measurement_unit_name = " + "'" + name + "'";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                id = c.getString(0);
            }
            while (c.moveToNext());
            return Long.parseLong(id);
        }
        c.close();
        return createUnit(name);
    }

    /**Logic - Recipes*/
    public void deleteRecipeForUser(long id) {
        deleteRecipesIngredients(id);
        deleteRecipe(id);
        deleteStep(id);
    }

    /**Logic - Ingredient*/
    public int countIngredientUsage(long ingredientId) {
        String count = "0";
        String selectQuery = "SELECT COUNT(*) FROM recipes_ingredients WHERE ingredient_id = '" + ingredientId + "'";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            count = c.getString(0);
        }
        c.close();
        return Integer.parseInt(count);
    }

    public List<Ingredient> getIngredientByRecipeId(int recipeId) {
        List<Ingredient> ingredients = new ArrayList<>();

        final List<String> ingredientIds = getItemIds(recipeId, "ingredient_id");
        final List<String> amountIds = getItemIds(recipeId, "measurement_qty_id");
        final List<String> unitIds = getItemIds(recipeId, "measurement_unit_id");
        final List<Integer> importanceIds = getImportanceByRecipeId(recipeId);

        for (int i = 0; i <= ingredientIds.size()-1; i++) {
            String selectIng = "SELECT ingredient_name FROM ingredients WHERE ingredient_id = " + ingredientIds.get(i);
            String selectAmount = "SELECT measurement_qty_amount FROM measurement_qty WHERE measurement_qty_id = " + amountIds.get(i);
            String selectUnit = "SELECT measurement_unit_name FROM measurement_units WHERE measurement_unit_id = " + unitIds.get(i);

            String ing = "";
            String amo = "";
            String uni = "";

            Cursor c = db.rawQuery(selectIng, null);
            if (c.moveToFirst() && c != null) {
                ing = c.getString(0);
                c.close();
            }

            c = db.rawQuery(selectAmount, null);
            if (c.moveToFirst() && c != null) {
                amo = c.getString(0);
                c.close();
            }

            c = db.rawQuery(selectUnit, null);
            if (c.moveToFirst() && c != null) {
                uni = c.getString(0);
                c.close();
            }

            if (amo == null) {
                amo = "0";
            }
            Ingredient ingredient = new Ingredient(ing, Float.parseFloat(amo), uni, importanceIds.get(i));
            ingredients.add(ingredient);
        }

        return ingredients;
    }


    /**Logic - Course*/
    public int getCourseId(String courseName) {
        int id = -1;
        String selectQuery = "SELECT course_id FROM courses WHERE course_name = '" + courseName + "'";
        Cursor nc = db.rawQuery(selectQuery, null);
        if (nc.moveToFirst()) {
            id = Integer.parseInt(nc.getString(0));
        }
        nc.close();
        return id;
    }


    /**Logic - FoodCategory*/
    public int getFoodCategoryId(String foodCategoryName) {
        int id = -1;
        String selectQuery = "SELECT food_category_id FROM food_categories WHERE food_category_name = '" + foodCategoryName + "'";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            id = Integer.parseInt(c.getString(0));
        }
        c.close();
        return id;
    }


    /**Logic - Ingredient*/
    public long getIngredientId(String name) {
        String ingId = "";
        String selectQuery = "SELECT ingredient_id FROM ingredients WHERE ingredient_name = " + "'" + name + "'";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                ingId = c.getString(0);
            }
            while (c.moveToNext());
            return Long.parseLong(ingId);
        }
        c.close();
        return -1;
    }


    /**Logic - Importance*/
    public List<Integer> getImportanceByRecipeId(long recipeId) {
        List<Integer> importance = new ArrayList<>();

        String selectQuery = "SELECT importance_value FROM recipes_ingredients WHERE recipe_id = " + recipeId;
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                importance.add(Integer.parseInt(c.getString(0)));
            }
            while (c.moveToNext());
            c.close();
        }
        return importance;
    }


    /**Logic - RecipesIngredients*/
    public List<String> getItemIds(int recipeId, String item) { //ingredient_id, measurement_qty_id, measurement_unit_id
        List<String> ids = new ArrayList<>();
        String selectQuery = "SELECT " + item + " FROM recipes_ingredients WHERE recipe_id = " + recipeId;
        c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                ids.add(id);
            }
            while (c.moveToNext());
        }
        c.close();
        return ids;
    }

    public Map<Long, String> getMapIngredients() {
        Map<Long, String> ingredients = new HashMap<>();
        String selectQuery = "SELECT * FROM ingredients";
        c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                ingredients.put(Long.parseLong(c.getString(0)), c.getString(1));
            }
            while (c.moveToNext());
        }
        c.close();
        return ingredients;
    }

}