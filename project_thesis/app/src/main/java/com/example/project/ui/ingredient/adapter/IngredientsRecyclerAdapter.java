package com.example.project.ui.ingredient.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngredientsRecyclerAdapter extends RecyclerView.Adapter<IngredientsRecyclerAdapter.ViewHolder> implements Filterable {

    private final Context context;
    private EditText name;
    public final Map<Long, String> list, listFull;
    public List<String> ingList;
    private IngredientAdapterListener listener;
    private AlertDialog.Builder builder;

    public IngredientsRecyclerAdapter(Map<Long, String> list, Context context) {
        this.list = list;
        this.listFull = new HashMap<>(list);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grocery_list_row, null);
        return new IngredientsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        builder = new AlertDialog.Builder(context);

        ingList = new ArrayList<>(list.values());

        holder.getName().setText(ingList.get(position));

        holder.modify.setOnClickListener(v -> {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View mView = layoutInflater.inflate(R.layout.ingredient_dialog, null);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setView(mView);

            name = mView.findViewById(R.id.d_ingredient);
            name.setText(ingList.get(position));

            alertDialog.setPositiveButton("Mentés", (dialog, which) -> passGroceryItemAt(ingList.get(position)));
            alertDialog.setNegativeButton("Mégse", (dialog, which) -> dialog.cancel());
            alertDialog.create().show();

        });

        holder.remove.setOnClickListener(v -> {
            String ingredient = ingList.get(position);

            showDeleteAlertDialog(ingredient);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return ingredientsFilter;
    }

    private  Filter ingredientsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Map<Long, String> filteredList = new HashMap<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.putAll(listFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Map.Entry<Long, String> entry : listFull.entrySet()) {
                    if (entry.getValue().toLowerCase().contains(filterPattern)) {
                        filteredList.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.putAll((Map<? extends Long, ? extends String>) results.values);
            notifyDataSetChanged();
        }
    };

    public void passGroceryItemAt(String oldIng) {
        String ingredient = getIngredientStringFromUser();
        listener.passToBeUpdatedIngredient(getKeyByValue(oldIng), ingredient);
    }

    public String getIngredientStringFromUser() {
        return (isEmpty(name)) ? null :  name.getText().toString().trim().toLowerCase();
    }

    private boolean isEmpty(TextView text) {
        return text.getText().toString().trim().length() == 0;
    }

    public long getKeyByValue(String value) {
        for (Map.Entry<Long, String> entry : list.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public void showDeleteAlertDialog(String item) {
        builder.setTitle("Megerősítés")
                .setMessage("Biztosan törli a(z) " + item + " terméket?")
                .setCancelable(true)
                .setPositiveButton("Törlés", (dialog, which) -> listener.passToBeDeletedIngredient(getKeyByValue(item)))
                .setNegativeButton("Mégse", (dialog, which) -> dialog.cancel())
                .show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final ImageView remove, modify;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.l_item);

            remove = view.findViewById(R.id.l_remove);
            modify = view.findViewById(R.id.l_modify);

            view.setTag(this);

        }

        public TextView getName() {
            return name;
        }

    }

    public interface IngredientAdapterListener {
        void passToBeDeletedIngredient(long ingredientId);
        void passToBeUpdatedIngredient(long ingredientId, String ingredientName);
    }

    public void setIngredientAdapterListener(IngredientAdapterListener ingredientAdapterListener) {
        this.listener = ingredientAdapterListener;
    }

}
