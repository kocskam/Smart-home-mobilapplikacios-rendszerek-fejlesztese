package com.example.project.ui.recipes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.model.Ingredient;

import java.util.List;

public class IngRecyclerAdapter extends RecyclerView.Adapter<IngRecyclerAdapter.ViewHolder> {

    private Context context;
    public final List<Ingredient> list;
    private IngRecyclerClickListener ingListener;

    public IngRecyclerAdapter(@NonNull Context context, List<Ingredient> items, IngRecyclerClickListener ingListener) {
        list = items;
        this.context = context;
         this.ingListener = ingListener;
     }

     public IngRecyclerAdapter(@NonNull Context context, List<Ingredient> items) {
        list = items;
        this.context = context;
     }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngRecyclerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_row, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ing = "";
        Ingredient currentIng = list.get(position);
        if (currentIng.getAmount() == 0.0) {
            ing = "(" + currentIng.getImportance() + ") " + currentIng.getUnit() + " " + currentIng.getName();
        }
        else {
            ing = "(" + currentIng.getImportance() + ") " + currentIng.getAmount() + currentIng.getUnit() + " " + currentIng.getName();
        }
        holder.getIngredient().setText(ing);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView ingredient;

        public ViewHolder(View view) {
            super(view);
            ingredient = view.findViewById(R.id.ingredient);
            view.setOnClickListener(this);
        }

        public TextView getIngredient() {
            return ingredient;
        }

        @Override
        public void onClick(View v) {
            if (ingListener != null) {
                ingListener.ingRecyclerViewListClicked(v, this.getLayoutPosition());
            }
        }
    }

    public interface IngRecyclerClickListener {
        void ingRecyclerViewListClicked(View view, int position);
    }

}
