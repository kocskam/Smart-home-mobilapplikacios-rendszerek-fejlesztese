package com.example.project.ui.ingredient;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project.R;
import com.example.project.database.RecipeDatabaseAccess;
import com.example.project.ui.ingredient.adapter.IngredientsRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;

import java.util.Map;

public class IngredientsFragment extends Fragment implements IngredientsRecyclerAdapter.IngredientAdapterListener {

    private static Toast t;
    private static Context context;

    private RecyclerView ingredientsRecycler;
    private EditText search;
    private EditText name;
    private FloatingActionButton fabAdd;

    private Map<Long, String> ingredients;

    private RecipeDatabaseAccess db;
    private IngredientsRecyclerAdapter ingredientAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ingredients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        db = RecipeDatabaseAccess.getInstance(context);

        fabAdd = view.findViewById(R.id.fabAddIng);

        ingredientsRecycler = view.findViewById(R.id.ingredientsList);
        search = view.findViewById(R.id.ingSearch);

        setupListView();
        filterRecycler();

        fabAdd.setOnClickListener(v -> showIngredientDialog());
    }

    public void setupListView() {
        db.open();
        ingredients = db.getMapIngredients();

        ingredientAdapter = new IngredientsRecyclerAdapter(ingredients, context);
        ingredientAdapter.setIngredientAdapterListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ingredientsRecycler.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        ingredientsRecycler.addItemDecoration(dividerItemDecoration);
        ingredientsRecycler.setLayoutManager(layoutManager);
        ingredientsRecycler.setItemAnimator(new DefaultItemAnimator());
        ingredientsRecycler.setAdapter(ingredientAdapter);

        db.close();
    }

    public void filterRecycler() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ingredientAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void showIngredientDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View mView = layoutInflater.inflate(R.layout.ingredient_dialog, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(mView);

        name = mView.findViewById(R.id.d_ingredient);

        alertDialog.setPositiveButton("Mentés", (dialog, which) -> createIngredientIfNotExists());
        alertDialog.setNegativeButton("Mégse", (dialog, which) -> dialog.cancel());
        alertDialog.create().show();
    }

    public void createIngredientIfNotExists() {
        String newIngredient = getIngredientStringFromUser();
        if (ingredients.containsValue(newIngredient)) {
            makeToast("Már van ilyen nevű hozzávaló!");
        }
        else {
            db.open();
            db.createIngredient(newIngredient);
        }
        setupListView();
        db.close();
    }

    public String getIngredientStringFromUser() {
        if (!(isEmpty(name))) {
            return name.getText().toString().trim().toUpperCase();
        }
        return null;
    }

    private boolean isEmpty(EditText text) {
        return text.getText().toString().trim().length() == 0;
    }

    @Override
    public void passToBeDeletedIngredient(long ingredientId) {
        db.open();
        if (db.countIngredientUsage(ingredientId) == 0) {
            db.deleteIngredient(ingredientId);
            makeToast("Törölve.");
            setupListView();
        }
        makeToast("Ezt a hozzávalót használják egy receptben.");
        db.close();
    }

    @Override
    public void passToBeUpdatedIngredient(long ingredientId, String ingredientName) {
        if (ingredientId == -1) {
            makeToast("Nem lehet üres a mező!");
        }
        else {
            db.open();
            db.updateIngredient(ingredientId, ingredientName);
            setupListView();
            db.close();
        }

    }

    public static void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        t.show();
    }

}