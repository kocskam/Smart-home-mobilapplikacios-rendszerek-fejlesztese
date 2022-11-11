package com.example.project.ui.recipes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipesListViewAdapter extends RecyclerView.Adapter<RecipesListViewAdapter.ViewHolder> implements Filterable {

    private Context context;
    public final List<Recipe> list, listFull;
    private View.OnClickListener listener;

    public RecipesListViewAdapter(@NonNull Context context, List<Recipe> items) {
        this.context = context;
        list = items;
        listFull = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_row, null);

        return new RecipesListViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.getName().setText(list.get(position).getName());
        holder.getCourse().setText(list.get(position).getCourse().name);
        holder.getCategory().setText(list.get(position).getFoodCategory().name);
        holder.getTime().setText(list.get(position).getCookTime() + "perc");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        this.listener = itemClickListener;
    }

    @Override
    public Filter getFilter() {
        return recipesFilter;
    }

    private final Filter recipesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Recipe> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Recipe r : listFull) {
                    if (r.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(r);
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
            list.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name, time, course, category;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.recipeName);
            time = view.findViewById(R.id.cookTime);
            course = view.findViewById(R.id.r_course);
            category = view.findViewById(R.id.r_category);

            view.setTag(this);
            view.setOnClickListener(listener);

        }

        public TextView getName() {
            return name;
        }

        public TextView getTime() {
            return time;
        }

        public TextView getCourse() {
            return course;
        }

        public TextView getCategory() {
            return category;
        }

    }

}
