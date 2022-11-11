package com.example.project.ui.recipes;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.database.RecipeDatabaseAccess;
import com.example.project.model.Ingredient;
import com.example.project.model.Recipe;
import com.example.project.model.RecipeDb;
import com.example.project.model.Step;
import com.example.project.ui.home.FilteredRecipes;
import com.example.project.ui.home.HomeFragment;
import com.example.project.ui.recipes.adapters.IngRecyclerAdapter;
import com.example.project.ui.recipes.adapters.StepRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

public class RecipeFragment extends Fragment {

    private Context context;

    private TextView name, time, course, category, description;
    private RecyclerView stepRecycler, ingredientRecycler;
    private FloatingActionMenu famRecipe;
    private FloatingActionButton fabDelete, fabModify;

    public final RecipeDb recipe;
    public final int fragment;

    private RecipeDatabaseAccess db;
    private StepRecyclerAdapter stepAdapter;
    private IngRecyclerAdapter ingAdapter;

    private AlertDialog.Builder builder;

    public RecipeFragment(RecipeDb recipe, int fragment) {
        this.recipe = recipe;
        this.fragment = fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        db = RecipeDatabaseAccess.getInstance(context);
        builder = new AlertDialog.Builder(context);

        name = view.findViewById(R.id.r_name);
        time = view.findViewById(R.id.c_time);
        course = view.findViewById(R.id.r_course);
        category = view.findViewById(R.id.r_category);
        description = view.findViewById(R.id.r_description);

        famRecipe = view.findViewById(R.id.famRecipe);
        fabDelete = view.findViewById(R.id.fabDeleteRecipe);
        fabModify = view.findViewById(R.id.fabModifyRecipe);

        if (fragment == R.id.fragmentFiltered) {
            famRecipe.setVisibility(View.INVISIBLE);
        }


        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            Fragment nextFrag = new RecipesFragment();
            if (fragment == R.id.fragmentFiltered) {
                List<Recipe> recipes = HomeFragment.getRecipes();
                nextFrag = new FilteredRecipes(recipes);
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                getFragmentManager().beginTransaction()
                        .replace(fragment, nextFrag)
                        .addToBackStack("RecipesFragment")
                        .commit();

                return true;
            }
            return false;
        });

        fabDelete.setOnClickListener(v -> showDeleteAlertDialog(recipe.getRecipe()));

        fabModify.setOnClickListener(v -> initNewRecipeFragment());

        ingredientRecycler = view.findViewById(R.id.ing_list);
        stepRecycler = view.findViewById(R.id.step_list);

        setTexts();

        setupStepListView();
        setupIngsListView();

    }

    public void showDeleteAlertDialog(Recipe recipe) {
        builder.setTitle("Megerősítés")
                .setMessage("Biztosan törölni szeretnéd a(z) "+ recipe.getName() + " nevű receptet?")
                .setCancelable(true)
                .setPositiveButton("Törlés", (dialog, which) -> {deleteRecipe(recipe.getId()); backToThePrevFragment();})
                .setNegativeButton("Mégse", (dialog, which) -> dialog.cancel())
                .show();
    }

    public void backToThePrevFragment() {
        RecipesFragment nextFrag = new RecipesFragment();
        getFragmentManager().beginTransaction()
                .replace(fragment, nextFrag)
                .addToBackStack("RecipesFragment")
                .commit();
    }

    public void deleteRecipe(long id) {
        db.open();
        db.deleteRecipeForUser(id);
        db.close();
    }

    private void initNewRecipeFragment() {
        db.open();
        NewRecipeFragment nextFrag = new NewRecipeFragment(recipe);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, nextFrag)
                .addToBackStack("RecipeFragment")
                .commit();
        db.close();
    }

    public void setTexts() {
        name.setText(recipe.getRecipe().getName());
        time.setText(recipe.getRecipe().getCookTime() + "perc");
        course.setText(recipe.getRecipe().getCourse().name);
        category.setText(recipe.getRecipe().getFoodCategory().name);
        description.setText(recipe.getRecipe().getDescription());
    }

    public void setupStepListView() {
        List<Step> steps = recipe.getSteps();

        stepAdapter = new StepRecyclerAdapter(context, steps);

        stepRecycler.setLayoutManager(new LinearLayoutManager(context));
        stepRecycler.setItemAnimator(new DefaultItemAnimator());
        stepRecycler.setAdapter(stepAdapter);
    }

    public void setupIngsListView() {
        List<Ingredient> ingredients = recipe.getIngredients();

        ingAdapter = new IngRecyclerAdapter(context, ingredients);

        ingredientRecycler.setLayoutManager(new LinearLayoutManager(context));
        ingredientRecycler.setItemAnimator(new DefaultItemAnimator());
        ingredientRecycler.setAdapter(ingAdapter);
    }

}
