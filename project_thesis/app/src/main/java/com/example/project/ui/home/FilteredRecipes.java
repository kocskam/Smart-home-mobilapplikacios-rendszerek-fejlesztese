package com.example.project.ui.home;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.project.ui.recipes.RecipeFragment;
import com.example.project.ui.recipes.adapters.RecipesListViewAdapter;

import java.util.List;

public class FilteredRecipes extends Fragment {

    private Context context;

    private RecyclerView filteredRecipesRecycler;

    private List<Recipe> filteredRecipes;

    private RecipeDatabaseAccess db;
    private RecipesListViewAdapter recipeAdapter;

    public FilteredRecipes(List<Recipe> recipes) {
        this.filteredRecipes = recipes;
    }

    private View.OnClickListener onItemClickListener = v -> {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
        int pos = viewHolder.getAdapterPosition();
        initRecipeFragment(pos);
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filtered_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        db = RecipeDatabaseAccess.getInstance(context);

        filteredRecipesRecycler = view.findViewById(R.id.filtered_recipes_list);

        setupFilteredRecipesListView();

    }

    private void setupFilteredRecipesListView() {
        recipeAdapter = new RecipesListViewAdapter(context, filteredRecipes);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(filteredRecipesRecycler.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        filteredRecipesRecycler.addItemDecoration(dividerItemDecoration);
        filteredRecipesRecycler.setLayoutManager(layoutManager);
        filteredRecipesRecycler.setItemAnimator(new DefaultItemAnimator());
        filteredRecipesRecycler.setAdapter(recipeAdapter);
        recipeAdapter.setOnItemClickListener(onItemClickListener);

    }

    public void initRecipeFragment(int position) {
        db.open();

        RecipeDb recipeForFragment = db.getRecipeForFragment(filteredRecipes.get(position).getId());
        RecipeFragment nextFrag = new RecipeFragment(recipeForFragment, R.id.fragmentFiltered);

        filteredRecipesRecycler.setVisibility(View.INVISIBLE);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentFiltered, nextFrag)
                .commit();

        db.close();
    }

}
