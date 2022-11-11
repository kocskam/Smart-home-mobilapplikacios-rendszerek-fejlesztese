package com.example.project.ui.home;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.project.ui.home.adapter.SelectedListViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements SelectedListViewAdapter.SelectedItemListener {
    
    private static Toast t;
    private static Context context;
    private Dialog dialog;

    private TextView selectIngredient, textCourse, textCat, textIng;
    private EditText searchBar;
    private static ListView itemsListView;
    private RecyclerView selectedItemsListView;
    private Button showRecipes;
    private CheckBox isSoup, isDessert, isMainCourse, isBreakfast, isDinner, isLunch;

    private List<String> ingredients, selectedIngredients;
    private static List<Recipe> recipes;

    private RecipeDatabaseAccess db;
    private ArrayAdapter<String> adapter;
    private SelectedListViewAdapter selectedAdapter;

    private final String BREAKFAST = "reggeli";
    private final String LUNCH_AND_DINNER = "ebéd/vacsora";
    private final String LUNCH = "ebéd";
    private final String DINNER = "vacsora";
    private final String MAIN_COURSE = "főétel";
    private final String DESSERT = "desszert";
    private final String SOUP = "leves";
    private final int THE_MOST_IMPORTANT = 5;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        db = RecipeDatabaseAccess.getInstance(context);

        selectIngredient = view.findViewById(R.id.text_ingredient);

        ingredients = getIngredients();
        selectedIngredients = new ArrayList<>();

        selectedAdapter = new SelectedListViewAdapter(context, selectedIngredients, this);

        selectIngredient.setOnClickListener(v -> {
            showDialog();

            /**dialog_searchable_spinner.xml:*/
            Button addIngredient = dialog.findViewById(R.id.addIngredient);
            addIngredient.setVisibility(View.INVISIBLE);

            searchBar = dialog.findViewById(R.id.search_bar);

            itemsListView = dialog.findViewById(R.id.list);
            selectedItemsListView = view.findViewById(R.id.list_selected);

            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, ingredients);

            setupListViews();

            filterList();

            itemsListView.setOnItemClickListener((parent, view1, position, id) -> {
                String item = adapter.getItem(position);
                addItem(item);
            });
        });

        isSoup = view.findViewById(R.id.c_soup);
        isDessert = view.findViewById(R.id.c_dessert);
        isMainCourse = view.findViewById(R.id.c_main);
        isBreakfast = view.findViewById(R.id.c_breakfast);
        isDinner = view.findViewById(R.id.c_dinner);
        isLunch = view.findViewById(R.id.c_lunch);

        showRecipes = view.findViewById(R.id.button_show);
        showRecipes.setOnClickListener(v -> {
            if (selectedIngredients.isEmpty()) {
                makeToast("Adj hozzávalókat a listához!");
            }
            else {
                initRecipeFragment();
            }
        });
        
        textCourse = view.findViewById(R.id.text_course);
        textCat = view.findViewById(R.id.text_category);
        textIng = view.findViewById(R.id.text_ingredients);

    }

    public List<String> getIngredients() {
        db.open();
        List<String> ingredients = db.getIngredients();
        db.close();
        return ingredients;
    }

    public void showDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_searchable_spinner);
        dialog.getWindow().setLayout(800, 1800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void setupListViews() {
        itemsListView.setAdapter(adapter);

        selectedItemsListView.setLayoutManager(new LinearLayoutManager(context));
        selectedItemsListView.setItemAnimator(new DefaultItemAnimator());
        selectedItemsListView.setAdapter(selectedAdapter);
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

    public void addItem(String item) {
        if (selectedIngredients.contains(item)) {
            makeToast(item + " már hozzá van adva.");
        } else {
            selectedIngredients.add(item);
            selectedItemsListView.setAdapter(selectedAdapter);
            makeToast(item + " hozzáadva.");
        }
    }

    public void initRecipeFragment() {

        recipes = transformMapToList();

        if (recipes.isEmpty()) {
            makeToast("A hozzávalókból nem készíthető recept.");
        }
        else {

            FilteredRecipes nextFrag = new FilteredRecipes(recipes);

            selectIngredient.setVisibility(View.INVISIBLE);
            searchBar.setVisibility(View.INVISIBLE);
            itemsListView.setVisibility(View.INVISIBLE);
            textCourse.setVisibility(View.INVISIBLE);
            textCat.setVisibility(View.INVISIBLE);
            textIng.setVisibility(View.INVISIBLE);
            selectedItemsListView.setVisibility(View.INVISIBLE);
            showRecipes.setVisibility(View.INVISIBLE);
            isSoup.setVisibility(View.INVISIBLE);
            isDessert.setVisibility(View.INVISIBLE);
            isMainCourse.setVisibility(View.INVISIBLE);
            isBreakfast.setVisibility(View.INVISIBLE);
            isDinner.setVisibility(View.INVISIBLE);
            isLunch.setVisibility(View.INVISIBLE);

            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentHome, nextFrag)
                    .commit();
        }

    }

    public List<Recipe> transformMapToList() {
        List<Map.Entry<Recipe, Integer>> sorted = getFilteredRecipes().entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toList());

        List<Recipe> recipes = new ArrayList<>();
        sorted.stream()
                .forEach(recipe -> recipes.add(recipe.getKey()));

        return recipes;
    }


    public Map<Recipe, Integer> getFilteredRecipes() {
        db.open();
        List<RecipeDb> recipes = db.getRecipeDbs();
        return recipes.stream()
                .filter(rec -> rec.getIngredients().stream().allMatch(ing -> ing.getImportance() != THE_MOST_IMPORTANT || selectedIngredients.contains(ing.getName())))
                .filter(rec -> categoryFilter().equals("") ?
                        rec.getRecipe().getFoodCategory().name.equals(SOUP)
                                || rec.getRecipe().getFoodCategory().name.equals(DESSERT)
                                || rec.getRecipe().getFoodCategory().name.equals(MAIN_COURSE)
                        :
                        rec.getRecipe().getFoodCategory().name.equals(categoryFilter())
                )
                .filter(rec -> courseFilter().equals("") ?
                        rec.getRecipe().getCourse().name.equals(BREAKFAST)
                                || rec.getRecipe().getCourse().name.equals(LUNCH)
                                || rec.getRecipe().getCourse().name.equals(DINNER)
                                || rec.getRecipe().getCourse().name.equals(LUNCH_AND_DINNER)
                        :
                        courseFilter().equals(LUNCH_AND_DINNER) ?
                                rec.getRecipe().getCourse().name.equals(LUNCH_AND_DINNER)
                                        || rec.getRecipe().getCourse().name.equals(LUNCH)
                                        || rec.getRecipe().getCourse().name.equals(DINNER)
                                :
                                rec.getRecipe().getCourse().name.equals(BREAKFAST)
                )
                .collect(Collectors.toMap(
                        RecipeDb::getRecipe,
                        rec -> rec.getIngredients().stream()
                                .mapToInt(Ingredient::getImportance)
                                .sum()
                ));
    }

    public String courseFilter() {
        if (isLunch.isChecked() && isDinner.isChecked()) {
            return LUNCH_AND_DINNER;
        }
        if (isLunch.isChecked()) {
            return LUNCH;
        }
        if (isDinner.isChecked()) {
            return DINNER;
        }
        return "";
    }

    public String categoryFilter() {
        if (isDessert.isChecked()) {
            return DESSERT;
        }
        if (isMainCourse.isChecked()) {
            return MAIN_COURSE;
        }
        if (isSoup.isChecked()) {
            return SOUP;
        }
        return "";
    }

    @Override
    public void passToBeDeletedIngredient(int position) {
        removeItem(position);
    }

    public void removeItem(int i) { ;
        makeToast(selectedIngredients.get(i) + " eltávolítva.");
        selectedIngredients.remove(i);
        selectedItemsListView.setAdapter(selectedAdapter);
    }

    public static void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        t.show();
    }

    public static List<Recipe> getRecipes() {
        return recipes;
    }
}