package com.example.project.ui.recipes;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.project.R;
import com.example.project.database.RecipeDatabaseAccess;
import com.example.project.model.Recipe;
import com.example.project.model.RecipeDb;
import com.example.project.ui.recipes.adapters.RecipesListViewAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;

import java.util.List;

public class RecipesFragment extends Fragment {

    private Context context;

    private RecyclerView recipesRecycler;
    private FloatingActionMenu fam;
    private FloatingActionButton fabAdd;
    private EditText search;

    private List<Recipe> recipes;

    private RecipeDatabaseAccess db;
    private RecipesListViewAdapter recipeAdapter;

    private View.OnClickListener onItemClickListener = v -> {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
        int pos = viewHolder.getAdapterPosition();
        initRecipeFragment(pos);
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        db = RecipeDatabaseAccess.getInstance(context);

        fam = view.findViewById(R.id.famRecipes);
        fabAdd = view.findViewById(R.id.fabAddRec);
        recipesRecycler = view.findViewById(R.id.recipe_list);
        search = view.findViewById(R.id.search);

        setupListView();
        filterRecycler();

        fabAdd.setOnClickListener(v -> initNewRecipeFragment());

    }

    public void initRecipeFragment(int position) {
        db.open();
        RecipeDb recipeForFragment = db.getRecipeForFragment(recipes.get(position).getId());

        RecipeFragment nextFrag = new RecipeFragment(recipeForFragment, R.id.fragment_layout);

        recipesRecycler.setVisibility(View.INVISIBLE);
        fabAdd.setVisibility(View.INVISIBLE);
        search.setVisibility(View.INVISIBLE);
        fam.setVisibility(View.INVISIBLE);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, nextFrag)
                .commit();
        db.close();
    }

    public void setupListView() {
        db.open();
        recipes = db.getRecipesForRow();

        recipeAdapter = new RecipesListViewAdapter(context, recipes);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recipesRecycler.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        recipesRecycler.addItemDecoration(dividerItemDecoration);
        recipesRecycler.setLayoutManager(layoutManager);
        recipesRecycler.setItemAnimator(new DefaultItemAnimator());
        recipesRecycler.setAdapter(recipeAdapter);
        recipeAdapter.setOnItemClickListener(onItemClickListener);

        db.close();
    }

    public void filterRecycler() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recipeAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void initNewRecipeFragment() {
        NewRecipeFragment nextFrag = new NewRecipeFragment();

        recipesRecycler.setVisibility(View.INVISIBLE);
        fabAdd.setVisibility(View.INVISIBLE);
        search.setVisibility(View.INVISIBLE);
        fam.setVisibility(View.INVISIBLE);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, nextFrag)
                .addToBackStack("RecipesFragment")
                .commit();
    }

}