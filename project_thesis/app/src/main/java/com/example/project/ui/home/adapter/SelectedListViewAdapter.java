package com.example.project.ui.home.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;

import java.util.List;

public class SelectedListViewAdapter extends RecyclerView.Adapter<SelectedListViewAdapter.ViewHolder> {

    private List<String> list;
    private Context context;
    private SelectedItemListener listener;

    public SelectedListViewAdapter(Context context, List<String> items, SelectedItemListener listener) {
        this.context = context;
        this.list = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_list_row, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.getNumber().setText(position + 1 + ".");
        holder.getIngredient().setText(list.get(position));

        holder.remove.setOnClickListener(v -> listener.passToBeDeletedIngredient(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView number, ingredient;
        private final ImageView remove;

        public ViewHolder(View view) {
            super(view);
            number = view.findViewById(R.id.number);
            ingredient = view.findViewById(R.id.ingredients);

            remove = view.findViewById(R.id.remove);

        }

        public TextView getNumber() {
            return number;
        }

        public TextView getIngredient() {
            return ingredient;
        }

    }

    public interface SelectedItemListener {
        void passToBeDeletedIngredient(int position);
    }

}
