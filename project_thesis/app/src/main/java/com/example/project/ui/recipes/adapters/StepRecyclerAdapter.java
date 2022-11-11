package com.example.project.ui.recipes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.model.Step;

import java.util.List;

public class StepRecyclerAdapter extends RecyclerView.Adapter<StepRecyclerAdapter.ViewHolder> {

    public final Context context;
    public final List<Step> list;
    private StepRecyclerClickListener stepListener;

    public StepRecyclerAdapter(@NonNull Context context, List<Step> items, StepRecyclerClickListener stepListener) {
        list = items;
        this.context = context;
        this.stepListener = stepListener;
     }

     public StepRecyclerAdapter(@NonNull Context context, List<Step> items) {
        list = items;
        this.context = context;
     }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StepRecyclerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.step_row, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.getNumber().setText(list.get(position).getStepNumber() + ". ");
        holder.getInfo().setText(list.get(position).getStepDescription());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView number, info;

        public ViewHolder(View view) {
            super(view);
            number = view.findViewById(R.id.stepNum);
            info = view.findViewById(R.id.stepInf);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (stepListener != null) {
                stepListener.stepRecyclerViewListClicked(v, this.getLayoutPosition());
            }
        }

        public TextView getNumber() {
            return number;
        }

        public TextView getInfo() {
            return info;
        }

    }

    public interface StepRecyclerClickListener {
        void stepRecyclerViewListClicked(View view, int position);
    }

}
