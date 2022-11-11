package com.example.project.ui.recipes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.database.RecipeDatabaseAccess;
import com.example.project.model.Course;
import com.example.project.model.FoodCategory;
import com.example.project.model.Ingredient;
import com.example.project.model.Recipe;
import com.example.project.model.RecipeDb;
import com.example.project.model.Step;
import com.example.project.ui.recipes.adapters.IngRecyclerAdapter;
import com.example.project.ui.recipes.adapters.StepRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;


public class NewRecipeFragment extends Fragment implements StepRecyclerAdapter.StepRecyclerClickListener, IngredientDialog.IngredientDialogListener, IngRecyclerAdapter.IngRecyclerClickListener {

    public static Toast t;
    private static Context context;

    private RecyclerView stepRecycler, ingRecycler;
    private EditText name, time, description, step;
    private Spinner course, category;
    private Button save, addStep;
    private TextView textViewIng;
    private EditText searchBar;

    private List<Step> steps;
    private List<Ingredient> ingredients;
    private int stepNumber;
    private Ingredient newIngredient;
    private RecipeDb recipe = null;
    private List<String> foodCategories;
    private List<String> courses;

    private RecipeDatabaseAccess db;
    private StepRecyclerAdapter stepAdapter;
    private IngRecyclerAdapter ingAdapter;
    private ArrayAdapter<String> adapter;

    private AlertDialog.Builder builder;
    private Dialog dialog;

    public NewRecipeFragment(RecipeDb recipe) {
        this.recipe = recipe;
    }

    public NewRecipeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        db = RecipeDatabaseAccess.getInstance(context);
        builder = new AlertDialog.Builder(context);

        name = view.findViewById(R.id.recipe_name);
        time = view.findViewById(R.id.recipe_time);
        description = view.findViewById(R.id.recipe_description);
        step = view.findViewById(R.id.recipe_step);

        textViewIng = view.findViewById(R.id.recipe_ingredient);

        save = view.findViewById(R.id.recipe_save);
        addStep = view.findViewById(R.id.add_step);

        course = view.findViewById(R.id.recipe_courses);
        category = view.findViewById(R.id.recipe_categories);

        stepRecycler = view.findViewById(R.id.steps);
        ingRecycler = view.findViewById(R.id.ings);

        initCategorySpinner();
        initCourseSpinner();

        ingredients = new ArrayList<>();
        steps = new ArrayList<>();
        stepNumber = 0;

        if (!(recipe == null)) {
            setEditTexts();
        }

        addStep.setOnClickListener(v -> getStepFromUser());

        save.setOnClickListener(v -> saveRecipe());

        textViewIng.setOnClickListener(v -> {
            showDialog();

            searchBar = dialog.findViewById(R.id.search_bar);
            ListView itemsListView = dialog.findViewById(R.id.list);
            Button addIngredient = dialog.findViewById(R.id.addIngredient);

            db.open();
            adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, db.getIngredients());
            db.close();
            itemsListView.setAdapter(adapter);

            filterList();

            addIngredient.setOnClickListener(v1 -> {
                String newIngredient = searchBar.getText().toString().toLowerCase().trim();
                if (newIngredient.equals("")) {
                    makeToast("Írj be egy hozzávaló nevét, amit hozzá szeretnél adni.");
                }
                else {
                    addNewIngredient(newIngredient);
                    dialog.dismiss();
                }
            });

            itemsListView.setOnItemClickListener((parent, view1, position, id) -> {
                String item = adapter.getItem(position);

                showIngDialog();

                newIngredient = new Ingredient();
                newIngredient.setName(item);

            });


        });

    }

    public void initCourseSpinner() {
        ArrayAdapter<String> courseListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getCourses());
        courseListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course.setAdapter(courseListAdapter);
    }

    public void initCategorySpinner() {
        ArrayAdapter<String> categoryListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getCategories());
        categoryListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryListAdapter);
    }

    public List<String> getCourses() {
        db.open();
        List<Course> rawCourses = db.getCourses();
        courses = new ArrayList<>();
        for (Course c : rawCourses) {
            courses.add(c.name);
        }
        db.close();
        return courses;
    }

    public List<String> getCategories() {
        db.open();
        List<FoodCategory> rawFoodCategories = db.getFoodCategories();
        foodCategories = new ArrayList<>();

        for (FoodCategory fc : rawFoodCategories) {
            foodCategories.add(fc.name);
        }
        db.close();
        return foodCategories;
    }

    public void setEditTexts() {
        name.setText(recipe.getRecipe().getName());
        time.setText(String.valueOf(recipe.getRecipe().getCookTime()));
        description.setText(recipe.getRecipe().getDescription());

        int categoryId = foodCategories.indexOf(recipe.getRecipe().getFoodCategory().name);
        category.setSelection(categoryId);

        int courseId = courses.indexOf(recipe.getRecipe().getCourse().name);
        course.setSelection(courseId);

        steps = recipe.getSteps();
        reloadStepListView();

        ingredients = recipe.getIngredients();
        reloadIngListView();

    }

    public void reloadStepListView() {
        stepAdapter = new StepRecyclerAdapter(context, steps, this);
        stepRecycler.setLayoutManager(new LinearLayoutManager(context));
        stepRecycler.setItemAnimator(new DefaultItemAnimator());
        stepRecycler.setAdapter(stepAdapter);
    }

    public void reloadIngListView() {
        ingAdapter = new IngRecyclerAdapter(context, ingredients, this);
        ingRecycler.setLayoutManager(new LinearLayoutManager(context));
        ingRecycler.setItemAnimator(new DefaultItemAnimator());
        ingRecycler.setAdapter(ingAdapter);
    }

    public void getStepFromUser() {
        String strStep = step.getText().toString().trim();
        if (strStep.equals("")) {
            makeToast("Recept elkészítése nem lehet üres!");
        }
        else {
            steps.add(new Step(++stepNumber, strStep));
            reloadStepListView();
            step.setText("");
        }
    }

    public void saveRecipe() {
        Recipe newRecipe = collectRecipeData();
        db.open();

        if (newRecipe == null || steps.size() == 0 || ingredients.size() == 0) {
            makeToast("Töltsd ki az összes kötelező mezőt!");
        }
        else {

            //update if exists
            if (!(recipe == null)) {
                db.updateRecipe(newRecipe, String.valueOf(recipe.getRecipe().getId()));
                db.updateSteps(steps, recipe.getRecipe().getId());
                db.deleteRecipesIngredients(recipe.getRecipe().getId());
                addRecipesIngredients(recipe.getRecipe().getId());

            }
            else {
                long recipe_id = db.createRecipe(newRecipe);
                db.createSteps(steps, recipe_id);
                addRecipesIngredients(recipe_id);
            }

            RecipesFragment nextFrag = new RecipesFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_layout, nextFrag)
                    .addToBackStack("RecipeFragment")
                    .commit();
        }
        db.close();
    }

    public Recipe collectRecipeData() {
        db.open();
        String recipeName = name.getText().toString().trim().toUpperCase();
        String recipeTime = time.getText().toString().trim();
        String recipeDescription = description.getText().toString().trim();
        String courseName = course.getSelectedItem().toString();
        String foodCategory = category.getSelectedItem().toString();

        if (isNull(recipeName, recipeTime, courseName, foodCategory)) {
            Course course = new Course(db.getCourseId(courseName), courseName);
            FoodCategory category = new FoodCategory(db.getFoodCategoryId(foodCategory), foodCategory);
            return new Recipe(null, recipeName, Integer.parseInt(recipeTime), recipeDescription, course, category);
        }
        db.close();
        return null;
    }

    public boolean isNull(String... values) {
        for (String s : values) {
            if (s.equals("")) {
                return false;
            }
        }
        return true;
    }

    public void showDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_searchable_spinner);
        dialog.setTitle("Hozzávaló adatai");
        dialog.getWindow().setLayout(800, 1800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void filterList() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    public void addNewIngredient(String ing) {
        db.open();
        List<String> ingredients = db.getIngredients();
        if (ingredients.contains(ing)) {
            makeToast(ing + " már hozzá van adva.");
        }
        else {
            db.createIngredient(ing);
            makeToast(ing + " hozzáadva az adatbázishoz.");
        }
        db.close();
    }

    public void showIngDialog() {
        FragmentManager manager = getFragmentManager();
        IngredientDialog ingredientDialog = new IngredientDialog();
        ingredientDialog.show(manager, "Üzenet");
        ingredientDialog.setIngredientListener(this);
    }

    public void addRecipesIngredients(long recipeId) {
        final List<Long> unitIds = addUnitIds(ingredients);
        final List<Long> amountIds = addAmountIds(ingredients);
        final List<Long> ingIds = addIngIds(ingredients);
        final List<Integer> impIds = addImpIds(ingredients);

        if (unitIds.size() == amountIds.size() && amountIds.size() == ingIds.size() && ingIds.size() == impIds.size()) {
            db.open();
            for (int i = 0; i < unitIds.size(); i++) {
                db.createRecipesIngredient(recipeId, ingIds.get(i), unitIds.get(i), amountIds.get(i), impIds.get(i));
            }
        }
        db.close();
    }

    public List<Long> addUnitIds(List<Ingredient> ingredients) {
        List<Long> unitIds = new ArrayList<>();
        for (Ingredient i : ingredients) {
            unitIds.add(db.upsertUnit(i.getUnit()));
        }
        return unitIds;
    }

    public List<Long> addAmountIds(List<Ingredient> ingredients) {
        List<Long> amountIds = new ArrayList<>();
        for (Ingredient i : ingredients) {
            amountIds.add(db.upsertQty(i.getAmount()));
        }
        return amountIds;
    }

    public List<Long> addIngIds(List<Ingredient> ingredients) {
        List<Long> ingIds = new ArrayList<>();
        for (Ingredient i : ingredients) {
            ingIds.add(db.getIngredientId(i.getName()));
        }
        return ingIds;
    }

    public List<Integer> addImpIds(List<Ingredient> ingredients) {
        List<Integer> impIds = new ArrayList<>();
        for (Ingredient i : ingredients) {
            impIds.add(i.getImportance());
        }
        return impIds;
    }

    @Override
    public void stepRecyclerViewListClicked(View view, int position) {
        showStepAlertDialog(steps.get(position));
    }

    @Override
    public void ingRecyclerViewListClicked(View view, int position) {
        showIngAlertDialog(ingredients.get(position));
    }

    @Override
    public void passToBeAddedIngredient(Ingredient ingredient) {
        newIngredient.setAmount(ingredient.getAmount());
        newIngredient.setUnit(ingredient.getUnit());
        newIngredient.setImportance(ingredient.getImportance());
        addItem(newIngredient);
    }

    public void addItem(Ingredient ingredient) {
        if (!(ingredients.contains(ingredient))) {
            ingredients.add(ingredient);
            reloadIngListView();
            makeToast(ingredient.getName() + " " + ingredient.getAmount() + ingredient.getUnit() + " hozzáadva.");
        } else {
            makeToast(ingredient.getName() + " " + ingredient.getAmount() + ingredient.getUnit() + " már hozzá van adva.");
        }
    }

    public void showStepAlertDialog(Step selectedStep) {
        builder.setTitle("Megerősítés")
                .setMessage("Biztosan ki szeretné törölni a  "+ selectedStep.getStepNumber() + ". lépést?")
                .setCancelable(true)
                .setPositiveButton("Törlés", (dialog, which) -> deleteStep(selectedStep))
                .setNegativeButton("Mégse", (dialog, which) -> dialog.cancel())
                .show();
    }

    public void deleteStep(Step selectedStep) {
        int index = steps.indexOf(selectedStep);
        steps.remove(selectedStep);
        transformSteps(index);
        reloadStepListView();
    }

    public void transformSteps(int index) {
        for (int i = index; i < steps.size(); i++) {
            steps.get(i).setStepNumber(steps.get(i).getStepNumber() - 1);
        }
        stepNumber--;
    }

    public void showIngAlertDialog(Ingredient selectedIng) {
        builder.setTitle("Megerősítés")
                .setMessage("Biztosan ki szeretné törölni a(z) "+ selectedIng.getName() + " hozzávalót?")
                .setCancelable(true)
                .setPositiveButton("Törlés", (dialog, which) -> deleteIngredient(selectedIng))

                .setNegativeButton("Mégse", (dialog, which) -> dialog.cancel())
                .show();
    }

    public void deleteIngredient(Ingredient selectedIng) {
        ingredients.remove(selectedIng);
        reloadIngListView();
    }

    public static void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        t.show();
    }

}